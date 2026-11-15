package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The single business entry point (facade). Every command goes through one of
 * these methods. All checks, state changes and formatting live here so that the
 * unit tests can reach them (line coverage is measured on this package only).
 */
public class CROBS {

    /** The superadmin is created with the program and keeps this fixed ID forever. */
    public static final String SUPERADMIN_ID = "U000";
    private static final String SUPERADMIN_NAME = "superadmin";

    // LinkedHashMap keeps insertion order, so listings come out in a stable order.
    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new LinkedHashMap<>();

    // One counter per entity: IDs are never reused, even after a deletion.
    private final IdGenerator userIds = new IdGenerator("U");
    private final IdGenerator roomIds = new IdGenerator("R");
    private final IdGenerator bookingIds = new IdGenerator("B");

    /** The one and only source of truth for "who is logged in" (null = nobody). */
    private User currentUser;

    public CROBS() {
        // The superadmin always exists and is not produced by the ID generator,
        // so its ID stays U000 on every run.
        users.put(SUPERADMIN_ID, new User(SUPERADMIN_ID, SUPERADMIN_NAME, Role.SUPERADMIN));
    }

    /** Use this instead of keeping a copy of the user in the controller. */
    public User getCurrentUser() {
        return currentUser;
    }

    /** REQ5 */
    public void login(String userId) {
        if (currentUser != null) {
            // One session at a time: switching users must go through logout first,
            // otherwise it is unclear on whose behalf the next command runs.
            throw new CrobsException.MalformedCommandException(
                    "Already logged in as " + currentUser.getId() + "; logout first");
        }
        currentUser = findUser(userId);
    }

    /** REQ6 */
    public void logout() {
        // Logging out while nobody is logged in is a user error, not a no-op.
        Permission.requireLogin(currentUser);
        currentUser = null;
    }

    /**
     * REQ2 -- module B's {@link User} does the validation, this only mints the ID.
     *
     * @param name the full name of the new user
     * @return the generated user ID
     */
    public String addUser(String name) {
        Permission.requireAdmin(currentUser);
        // Validate before minting an ID so that a rejected command does not burn one.
        if (name == null || name.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException("User name cannot be empty.");
        }
        String id = userIds.next();
        users.put(id, new User(id, name, Role.REGULAR));
        return id;
    }

    /** REQ3 -- also cancels this user's bookings. */
    public void removeUser(String userId) {
        Permission.requireAdmin(currentUser);
        User target = findUser(userId);
        if (target.getRole() != Role.REGULAR) {
            throw new CrobsException.PermissionDeniedException(
                    "Only a regular user can be removed; " + target.getId() + " is " + target.getRole());
        }
        // Minimal safe handling until module C lands: drop the bookings of the user.
        // TODO(REQ3/REQ10): module C decides whether a booking of a removed user is
        // deleted or kept as history; revisit once Booking semantics exist.
        bookings.values().removeIf(booking -> booking.getUserId().equals(target.getId()));
        users.remove(target.getId());
    }

    /** REQ4 -- superadmin only. */
    public void changeRole(String userId, String newRole) {
        Permission.requireSuperAdmin(currentUser);
        User target = findUser(userId);
        Role role = parseRole(newRole);            // MalformedCommandException on a bad label

        if (target.getRole() == Role.SUPERADMIN) {
            throw new CrobsException.PermissionDeniedException("The superadmin's role cannot be changed");
        }
        if (role == Role.SUPERADMIN) {
            // There is exactly one superadmin; promoting someone else would break REQ4.
            throw new CrobsException.PermissionDeniedException(
                    "Cannot promote " + target.getId() + " to superadmin; the role is unique");
        }
        // target is the same object as currentUser when a user edits themself,
        // so the session state follows automatically (no second copy to update).
        target.setRole(role);
    }

    /**
     * Looks a user up by ID. IDs are compared case-insensitively because they are
     * always displayed in upper case (U000, U001, ...).
     */
    private User findUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new CrobsException.MalformedCommandException("User ID is missing");
        }
        User user = users.get(userId.trim().toUpperCase(Locale.ROOT));
        if (user == null) {
            throw new CrobsException.NotFoundException("No such user: " + userId);
        }
        return user;
    }

    /**
     * Looks a room up by ID, with the same case-insensitive matching as
     * {@link #findUser(String)}.
     */
    private Room findRoom(String roomId) {
        if (roomId == null || roomId.isBlank()) {
            throw new CrobsException.MalformedCommandException("Room ID is missing");
        }
        Room room = rooms.get(roomId.trim().toUpperCase(Locale.ROOT));
        if (room == null) {
            throw new CrobsException.NotFoundException("No such room: " + roomId);
        }
        return room;
    }

    /** Maps a command word such as "admin" to the enum constant. */
    private static Role parseRole(String label) {
        if (label != null) {
            for (Role role : Role.values()) {
                if (role.name().equalsIgnoreCase(label.trim())) {
                    return role;
                }
            }
        }
        throw new CrobsException.MalformedCommandException(
                "Unknown role: " + label + " (expected admin, regular or superadmin)");
    }

    /**
     * REQ7 -- parsing and validation belong to module B's {@link Room}.
     *
     * @param roomType one of the four labels of {@link RoomType}
     * @param capacity the number of seats, as text
     * @param features the comma-separated features, or "-" for none
     * @return the generated room ID
     */
    public String addRoom(String roomType, String capacity, String features) {
        Permission.requireAdmin(currentUser);
        RoomType type = RoomType.fromLabel(roomType);
        // Both parsers throw before an ID is minted, so a bad value burns no ID.
        int seats = Room.parseCapacity(capacity);
        Set<String> featureSet = Room.parseFeatures(features);
        String id = roomIds.next();
        rooms.put(id, new Room(id, type, seats, featureSet));
        return id;
    }

    /** REQ8 -- the field rules live in module B's {@link Room#modify(String, String)}. */
    public void modifyRoom(String roomId, String field, String newValue) {
        Permission.requireAdmin(currentUser);
        findRoom(roomId).modify(field, newValue);
    }

    /** REQ9 -- also cancels the bookings of this room. */
    public void deleteRoom(String roomId) {
        Permission.requireAdmin(currentUser);
        Room room = findRoom(roomId);
        // Minimal safe handling so that no booking points at a removed room.
        // TODO(REQ9): module C must refine this to "cancel the future bookings only"
        // (past bookings are history); that rule needs Booking/TimeSlot semantics.
        bookings.values().removeIf(booking -> booking.getRoomId().equals(room.getId()));
        rooms.remove(room.getId());
    }

    /** REQ10 */
    public String book(String roomId, String date, String startTime, String endTime, String purpose) {
        throw new UnsupportedOperationException("TODO: book");
    }

    /** REQ11 */
    public void modifyBooking(String bookingId, String roomId, String date,
                              String startTime, String endTime, String purpose) {
        throw new UnsupportedOperationException("TODO: modifyBooking");
    }

    /** REQ12 */
    public void deleteBooking(String bookingId) {
        throw new UnsupportedOperationException("TODO: deleteBooking");
    }

    /** REQ13 */
    public List<String> search(String date, String startTime, String endTime,
                               String minCapacity, String features) {
        throw new UnsupportedOperationException("TODO: search");
    }

    /** REQ14 -- regular users only see their own user IDs. */
    public List<String> schedule(String roomId, String weekStartDate) {
        throw new UnsupportedOperationException("TODO: schedule");
    }

    /** REQ15 */
    public String listRoom(String roomId) {
        throw new UnsupportedOperationException("TODO: listRoom");
    }

    /** REQ16 -- sorted by room ID ascending. */
    public List<String> listAllRooms() {
        throw new UnsupportedOperationException("TODO: listAllRooms");
    }

    /** REQ17 -- sorted by date and start time. */
    public List<String> listBookings() {
        throw new UnsupportedOperationException("TODO: listBookings");
    }
}
