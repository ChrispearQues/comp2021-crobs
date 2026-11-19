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

    /** The superadmin is created with the program and keeps this fixed ID forever. */
    public static final String SUPERADMIN_ID = "U000";

    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new LinkedHashMap<>();

    private final IdGenerator userIds = new IdGenerator("U");
    private final IdGenerator roomIds = new IdGenerator("R");
    private final IdGenerator bookingIds = new IdGenerator("B");

    private User currentUser;

    /** Creates a fresh system with no user logged in and one superadmin. */
    public CROBS() {
        // The superadmin is not produced by the ID generator, so its ID is U000
        // on every run and the first real user keeps the ID U001.
        users.put(SUPERADMIN_ID, new User(SUPERADMIN_ID, "superadmin", Role.SUPERADMIN));
    }

    /**
     * Tells who is logged in.
     *
     * @return the logged-in user, or null when nobody is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * REQ5: logs in as an existing user.
     *
     * @param userId the ID of the user to log in
     * @throws CrobsException.NotFoundException      when no user has this ID
     * @throws CrobsException.MalformedCommandException when that user is already logged in
     */
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

    /** REQ6: logs out the current user. */
    public void logout() {
        currentUser = null;
    }

    /**
     * REQ2: creates a regular user.
     *
     * @param name the full name of the new user
     * @return the confirmation line, including the generated user ID
     * @throws CrobsException.PermissionDeniedException when the caller is not an admin
     * @throws CrobsException.MalformedCommandException when the name is blank
     */
    public String addUser(String name) {
        Permission.requireAdmin(currentUser);
        String id = userIds.next();
        users.put(id, new User(id, requireText(name, CrobsMessages.EMPTY_NAME), Role.REGULAR));
        return String.format(CrobsMessages.USER_CREATED, id);
    }

    /**
     * REQ3: removes a regular user and cancels the bookings that user made.
     *
     * @param userId the ID of the user to remove
     * @throws CrobsException.PermissionDeniedException when the caller is not an admin,
     *         or when the target is not a regular user
     * @throws CrobsException.NotFoundException          when no user has this ID
     */
    public void removeUser(String userId) {
        Permission.requireAdmin(currentUser);
        User target = requireUser(userId);
        if (target.getRole() != Role.REGULAR) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.REGULAR_ONLY);
        }
        bookings.values().removeIf(booking -> booking.getUserId().equals(userId));
        users.remove(userId);
    }

    /**
     * REQ4: changes the role of another user. Only the superadmin may do this.
     *
     * @param userId  the ID of the user whose role changes
     * @param newRole "admin" or "regular"
     * @throws CrobsException.PermissionDeniedException when the caller is not the
     *         superadmin, or when the target is the superadmin
     * @throws CrobsException.NotFoundException          when no user has this ID
     * @throws CrobsException.MalformedCommandException  when the role name is unknown
     */
    public void changeRole(String userId, String newRole) {
        Permission.requireSuperAdmin(currentUser);
        User target = requireUser(userId);
        if (target.getRole() == Role.SUPERADMIN) {
            throw new CrobsException.PermissionDeniedException(CrobsMessages.SUPERADMIN_FIXED);
        }
        target.setRole(parseRole(newRole));
    }

    /**
     * REQ7: creates a room.
     *
     * @param roomType one of lectureTheatre, computerLab, seminarRoom, meetingRoom
     * @param capacity the number of seats, as text
     * @param features the comma-separated features, or "-" for none
     * @return the confirmation line, including the generated room ID
     * @throws CrobsException.PermissionDeniedException when the caller is not an admin
     * @throws CrobsException.MalformedCommandException when the type, capacity or features are invalid
     */
    public String addRoom(String roomType, String capacity, String features) {
        Permission.requireAdmin(currentUser);
        RoomType type = RoomType.fromLabel(roomType);
        int seats = Room.parseCapacity(capacity, CrobsMessages.INVALID_CAPACITY);
        String id = roomIds.next();
        rooms.put(id, new Room(id, type, seats, Room.parseFeatures(features)));
        return String.format(CrobsMessages.ROOM_CREATED, id);
    }

    /**
     * REQ8: changes one field of a room.
     *
     * @param roomId   the room to change
     * @param field    "type", "capacity" or "features"
     * @param newValue the new value of that field
     * @throws CrobsException.PermissionDeniedException when the caller is not an admin
     * @throws CrobsException.NotFoundException          when the room does not exist
     * @throws CrobsException.MalformedCommandException  when the field or the value is invalid
     */
    public void modifyRoom(String roomId, String field, String newValue) {
        Permission.requireAdmin(currentUser);
        requireRoom(roomId).modify(field, newValue);
    }

    /**
     * REQ9: deletes a room and cancels its bookings that are not in the past.
     *
     * @param roomId the room to delete
     * @throws CrobsException.PermissionDeniedException when the caller is not an admin
     * @throws CrobsException.NotFoundException          when the room does not exist
     */
    public void deleteRoom(String roomId) {
        Permission.requireAdmin(currentUser);
        Room room = requireRoom(roomId);
        LocalDate today = LocalDate.now();
        bookings.values().removeIf(booking -> booking.getRoomId().equals(room.getId())
                && !DateFormats.parseDate(booking.getTimeSlot().getDate()).isBefore(today));
        rooms.remove(room.getId());
    }

    /**
     * REQ10: books a room for the logged-in user.
     *
     * @param roomId    the room to book
     * @param date      the day, as yyyy-MM-dd
     * @param startTime the start time, as HH:mm
     * @param endTime   the end time, as HH:mm
     * @param purpose   why the room is needed
     * @return the confirmation line, including the generated booking ID
     * @throws CrobsException.PermissionDeniedException when nobody is logged in
     * @throws CrobsException.NotFoundException          when the room does not exist
     * @throws CrobsException.ConflictException          when the slot is already taken in that room
     * @throws CrobsException.MalformedCommandException  when a date, time, interval or purpose is invalid
     */
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

    /**
     * REQ11: changes an existing booking of the logged-in user.
     *
     * @param bookingId the booking to change
     * @param roomId    the new room
     * @param date      the new day, as yyyy-MM-dd
     * @param startTime the new start time, as HH:mm
     * @param endTime   the new end time, as HH:mm
     * @param purpose   the new purpose
     * @throws CrobsException.PermissionDeniedException when nobody is logged in,
     *         or when the booking belongs to somebody else
     * @throws CrobsException.NotFoundException          when the booking or the room does not exist
     * @throws CrobsException.ConflictException          when the new slot is already taken
     * @throws CrobsException.MalformedCommandException  when a date, time, interval or purpose is invalid
     */
    public void modifyBooking(String bookingId, String roomId, String date,
                              String startTime, String endTime, String purpose) {
        Permission.requireLogin(currentUser);
        Booking booking = requireOwnedBooking(bookingId);
        Room room = requireRoom(roomId);
        TimeSlot slot = parseSlot(date, startTime, endTime);
        requireFree(room.getId(), slot, booking.getId());
        booking.modify(room.getId(), slot, requireText(purpose, CrobsMessages.EMPTY_PURPOSE));
    }

    /**
     * REQ12: deletes a booking of the logged-in user.
     *
     * @param bookingId the booking to delete
     * @throws CrobsException.PermissionDeniedException when nobody is logged in,
     *         or when the booking belongs to somebody else
     * @throws CrobsException.NotFoundException          when the booking does not exist
     */
    public void deleteBooking(String bookingId) {
        Permission.requireLogin(currentUser);
        Booking booking = requireOwnedBooking(bookingId);
        bookings.remove(booking.getId());
    }

    /**
     * REQ13: lists the rooms that are free and match the request.
     *
     * @param date        the day, as yyyy-MM-dd
     * @param startTime   the start time, as HH:mm
     * @param endTime     the end time, as HH:mm
     * @param minCapacity the smallest acceptable capacity
     * @param features    the required features, comma separated, or "-" for none
     * @return one line per matching room
     * @throws CrobsException.PermissionDeniedException when nobody is logged in
     * @throws CrobsException.MalformedCommandException when a date, time, interval or capacity is invalid
     */
    public List<String> search(String date, String startTime, String endTime,
                               String minCapacity, String features) {
        Permission.requireLogin(currentUser);
        return new RoomSearch(rooms.values(), bookings.values())
                .search(date, startTime, endTime, minCapacity, features);
    }

    /**
     * REQ14: shows the bookings of one room for one week. A regular user only
     * sees their own user IDs.
     *
     * @param roomId        the room whose schedule is requested
     * @param weekStartDate the first day of the week, as yyyy-MM-dd
     * @return one line per booking
     * @throws CrobsException.PermissionDeniedException when nobody is logged in
     * @throws CrobsException.NotFoundException          when the room does not exist
     * @throws CrobsException.MalformedCommandException  when the date is invalid
     */
    public List<String> schedule(String roomId, String weekStartDate) {
        Permission.requireLogin(currentUser);
        return new ScheduleView(rooms, bookings.values()).week(roomId, weekStartDate, currentUser);
    }

    /**
     * REQ15: shows one room.
     *
     * @param roomId the room to show
     * @return one line describing the room
     * @throws CrobsException.PermissionDeniedException when nobody is logged in
     * @throws CrobsException.NotFoundException          when the room does not exist
     */
    public String listRoom(String roomId) {
        Permission.requireLogin(currentUser);
        return requireRoom(roomId).describe();
    }

    /**
     * REQ16: shows every room, sorted by room ID.
     *
     * @return one line per room
     * @throws CrobsException.PermissionDeniedException when nobody is logged in
     */
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

    /**
     * REQ17: shows every booking of the logged-in user, sorted by date and start time.
     *
     * @return one line per booking
     * @throws CrobsException.PermissionDeniedException when nobody is logged in
     */
    public List<String> listBookings() {
        Permission.requireLogin(currentUser);
        return new BookingListView(bookings.values()).of(currentUser);
    }

    // ---- undo / redo support (BON2) ----------------------------------------

    /**
     * Copies the whole state of the system.
     *
     * @return a snapshot that undo or redo can put back later
     */
    public Snapshot snapshot() {
        return new Snapshot(users, rooms, bookings,
                userIds.getCounter(), roomIds.getCounter(), bookingIds.getCounter(),
                currentUser == null ? null : currentUser.getId());
    }

    /**
     * Replaces the whole state of the system with a snapshot. The logged-in
     * user is restored by ID, so undo neither logs anybody in nor out.
     *
     * @param snapshot the state to put back
     */
    public void restore(Snapshot snapshot) {
        users.clear();
        users.putAll(snapshot.copyUsers());
        rooms.clear();
        rooms.putAll(snapshot.copyRooms());
        bookings.clear();
        bookings.putAll(snapshot.copyBookings());
        userIds.setCounter(snapshot.getNextUserId());
        roomIds.setCounter(snapshot.getNextRoomId());
        bookingIds.setCounter(snapshot.getNextBookingId());
        String currentId = snapshot.getCurrentUserId();
        currentUser = currentId == null ? null : users.get(currentId);
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
     *                       does not conflict with itself
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
