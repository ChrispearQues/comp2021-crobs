package hk.edu.polyu.comp.comp2021.crobs.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The single business entry point (facade). Every command goes through one of
 * these methods. All checks, state changes and formatting live here so that the
 * unit tests can reach them (line coverage is measured on this package only).
 */
public class CROBS {

    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new LinkedHashMap<>();

    private final IdGenerator userIds = new IdGenerator("U");
    private final IdGenerator roomIds = new IdGenerator("R");
    private final IdGenerator bookingIds = new IdGenerator("B");

    private User currentUser;

    /** A fresh system with no user logged in and one superadmin (see the overview). */
    public CROBS() {
        String id = userIds.next();
        users.put(id, new User(id, "superadmin", Role.SUPERADMIN));
    }

    /** The logged-in user, or {@code null} when nobody is logged in. */
    public User getCurrentUser() {
        return currentUser;
    }

    /** REQ5 */
    public void login(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new CrobsException.NotFoundException(
                    String.format(CrobsMessages.UNKNOWN_USER, userId));
        }
        if (user == currentUser) {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.ALREADY_LOGGED_IN, userId));
        }
        currentUser = user;
    }

    /** REQ6 */
    public void logout() {
        currentUser = null;
    }

    /** REQ2 @return the confirmation line, including the generated user ID. */
    public String addUser(String name) {
        Permission.requireAdmin(currentUser);
        String id = userIds.next();
        users.put(id, new User(id, requireText(name, CrobsMessages.EMPTY_NAME), Role.REGULAR));
        return String.format(CrobsMessages.USER_CREATED, id);
    }

    /** REQ3 -- also cancels this user's bookings. */
    public void removeUser(String userId) {
        Permission.requireAdmin(currentUser);
        User target = requireUser(userId);
        if (target.getRole() != Role.REGULAR) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.REGULAR_ONLY);
        }
        bookings.values().removeIf(booking -> booking.getUserId().equals(userId));
        users.remove(userId);
    }

    /** REQ4 -- superadmin only. */
    public void changeRole(String userId, String newRole) {
        Permission.requireSuperAdmin(currentUser);
        User target = requireUser(userId);
        if (target.getRole() == Role.SUPERADMIN) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.SUPERADMIN_FIXED);
        }
        target.setRole(parseRole(newRole));
    }

    /** REQ7 @return the confirmation line, including the generated room ID. */
    public String addRoom(String roomType, String capacity, String features) {
        Permission.requireAdmin(currentUser);
        RoomType type = RoomType.fromLabel(roomType);
        int seats = Room.parseCapacity(capacity, CrobsMessages.INVALID_CAPACITY);
        String id = roomIds.next();
        rooms.put(id, new Room(id, type, seats, Room.parseFeatures(features)));
        return String.format(CrobsMessages.ROOM_CREATED, id);
    }

    /** REQ8 */
    public void modifyRoom(String roomId, String field, String newValue) {
        Permission.requireAdmin(currentUser);
        requireRoom(roomId).modify(field, newValue);
    }

    /** REQ9 -- also cancels all future bookings of this room. */
    public void deleteRoom(String roomId) {
        Permission.requireAdmin(currentUser);
        Room room = requireRoom(roomId);
        LocalDate today = LocalDate.now();
        bookings.values().removeIf(booking -> booking.getRoomId().equals(room.getId())
                && !DateFormats.parseDate(booking.getTimeSlot().getDate()).isBefore(today));
        rooms.remove(room.getId());
    }

    /** REQ10 @return the confirmation line, including the generated booking ID. */
    public String book(String roomId, String date, String startTime, String endTime, String purpose) {
        Permission.requireLogin(currentUser);
        Room room = requireRoom(roomId);
        TimeSlot slot = parseSlot(date, startTime, endTime);
        requireFree(room.getId(), slot, null);
        String id = bookingIds.next();
        bookings.put(id, new Booking(id, currentUser.getId(), room.getId(), slot,
                requireText(purpose, CrobsMessages.EMPTY_PURPOSE)));
        return String.format(CrobsMessages.BOOKING_CREATED, id);
    }

    /** REQ11 */
    public void modifyBooking(String bookingId, String roomId, String date,
                              String startTime, String endTime, String purpose) {
        Permission.requireLogin(currentUser);
        Booking booking = requireOwnedBooking(bookingId);
        Room room = requireRoom(roomId);
        TimeSlot slot = parseSlot(date, startTime, endTime);
        requireFree(room.getId(), slot, booking.getId());
        booking.modify(room.getId(), slot, requireText(purpose, CrobsMessages.EMPTY_PURPOSE));
    }

    /** REQ12 */
    public void deleteBooking(String bookingId) {
        Permission.requireLogin(currentUser);
        Booking booking = requireOwnedBooking(bookingId);
        bookings.remove(booking.getId());
    }

    /** REQ13 */
    public List<String> search(String date, String startTime, String endTime,
                               String minCapacity, String features) {
        Permission.requireLogin(currentUser);
        return new RoomSearch(rooms.values(), bookings.values())
                .search(date, startTime, endTime, minCapacity, features);
    }

    /** REQ14 -- regular users only see their own user IDs. */
    public List<String> schedule(String roomId, String weekStartDate) {
        Permission.requireLogin(currentUser);
        return new ScheduleView(rooms, bookings.values()).week(roomId, weekStartDate, currentUser);
    }

    /** REQ15 */
    public String listRoom(String roomId) {
        Permission.requireLogin(currentUser);
        return requireRoom(roomId).describe();
    }

    /** REQ16 -- sorted by room ID ascending. */
    public List<String> listAllRooms() {
        Permission.requireLogin(currentUser);
        List<String> ids = new ArrayList<>(rooms.keySet());
        Collections.sort(ids);
        List<String> lines = new ArrayList<>();
        for (String id : ids) {
            lines.add(rooms.get(id).describe());
        }
        return lines;
    }

    /** REQ17 -- sorted by date and start time. */
    public List<String> listBookings() {
        Permission.requireLogin(currentUser);
        return new BookingListView(bookings.values()).of(currentUser);
    }

    // ---- helpers -----------------------------------------------------------

    private User requireUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new CrobsException.NotFoundException(
                    String.format(CrobsMessages.UNKNOWN_USER, userId));
        }
        return user;
    }

    private Room requireRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new CrobsException.NotFoundException(
                    String.format(CrobsMessages.UNKNOWN_ROOM, roomId));
        }
        return room;
    }

    /** The booking must exist and belong to the logged-in user. */
    private Booking requireOwnedBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new CrobsException.NotFoundException(
                    String.format(CrobsMessages.UNKNOWN_BOOKING, bookingId));
        }
        if (!booking.getUserId().equals(currentUser.getId())) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.NOT_OWNER);
        }
        return booking;
    }

    private static TimeSlot parseSlot(String date, String startTime, String endTime) {
        LocalDate day = DateFormats.parseDate(date);
        LocalTime start = DateFormats.parseTime(startTime);
        LocalTime end = DateFormats.parseTime(endTime);
        DateFormats.requireOrdered(start, end);
        return new TimeSlot(day.toString(), startTime, endTime);
    }

    /**
     * @param ignoredBooking ID of the booking being modified, so that a booking
     *                       does not conflict with itself.
     */
    private void requireFree(String roomId, TimeSlot slot, String ignoredBooking) {
        for (Booking booking : bookings.values()) {
            if (booking.getId().equals(ignoredBooking)) {
                continue;
            }
            if (!booking.getRoomId().equals(roomId)) {
                continue;
            }
            if (booking.getTimeSlot().overlaps(slot)) {
                throw new CrobsException.ConflictException(String.format(CrobsMessages.CONFLICT,
                        roomId, booking.getTimeSlot().getStartTime(),
                        booking.getTimeSlot().getEndTime(), booking.getTimeSlot().getDate()));
            }
        }
    }

    private static Role parseRole(String text) {
        if ("admin".equalsIgnoreCase(text)) {
            return Role.ADMIN;
        }
        if ("regular".equalsIgnoreCase(text)) {
            return Role.REGULAR;
        }
        throw new CrobsException.MalformedCommandException(
                String.format(CrobsMessages.INVALID_ROLE, text));
    }

    private static String requireText(String text, String message) {
        if (text == null || text.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException(message);
        }
        return text.trim();
    }
}
