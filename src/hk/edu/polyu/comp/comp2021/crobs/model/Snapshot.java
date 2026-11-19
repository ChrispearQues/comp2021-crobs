package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * A deep copy of the whole state of a {@link CROBS} system: its users, rooms,
 * bookings, the next number of each ID generator and who is logged in (BON2).
 *
 * <p>A snapshot never shares an object with the live system, and every call to
 * one of the {@code copy…} methods hands out a fresh copy, so neither undo nor
 * redo can be disturbed by a change made afterwards.</p>
 */
public final class Snapshot {

    private final Map<String, User> users;
    private final Map<String, Room> rooms;
    private final Map<String, Booking> bookings;
    private final int nextUserId;
    private final int nextRoomId;
    private final int nextBookingId;
    private final String currentUserId;

    /**
     * Copies the given state.
     *
     * @param users         the users to copy
     * @param rooms         the rooms to copy
     * @param bookings      the bookings to copy
     * @param nextUserId    the number the user ID generator will produce next
     * @param nextRoomId    the number the room ID generator will produce next
     * @param nextBookingId the number the booking ID generator will produce next
     * @param currentUserId the logged-in user, or null
     */
    public Snapshot(Map<String, User> users, Map<String, Room> rooms, Map<String, Booking> bookings,
                    int nextUserId, int nextRoomId, int nextBookingId, String currentUserId) {
        this.users = copyUsers(users);
        this.rooms = copyRooms(rooms);
        this.bookings = copyBookings(bookings);
        this.nextUserId = nextUserId;
        this.nextRoomId = nextRoomId;
        this.nextBookingId = nextBookingId;
        this.currentUserId = currentUserId;
    }

    /** @return a fresh copy of the users of this snapshot */
    public Map<String, User> copyUsers() { return copyUsers(users); }

    /** @return a fresh copy of the rooms of this snapshot */
    public Map<String, Room> copyRooms() { return copyRooms(rooms); }

    /** @return a fresh copy of the bookings of this snapshot */
    public Map<String, Booking> copyBookings() { return copyBookings(bookings); }

    /** @return the number the user ID generator had reached */
    public int getNextUserId() { return nextUserId; }

    /** @return the number the room ID generator had reached */
    public int getNextRoomId() { return nextRoomId; }

    /** @return the number the booking ID generator had reached */
    public int getNextBookingId() { return nextBookingId; }

    /** @return the ID of the user that was logged in, or null */
    public String getCurrentUserId() { return currentUserId; }

    private static Map<String, User> copyUsers(Map<String, User> source) {
        Map<String, User> copy = new LinkedHashMap<>();
        for (Map.Entry<String, User> entry : source.entrySet()) {
            User user = entry.getValue();
            copy.put(entry.getKey(), new User(user.getId(), user.getName(), user.getRole()));
        }
        return copy;
    }

    private static Map<String, Room> copyRooms(Map<String, Room> source) {
        Map<String, Room> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Room> entry : source.entrySet()) {
            Room room = entry.getValue();
            copy.put(entry.getKey(), new Room(room.getId(), room.getType(), room.getCapacity(),
                    new LinkedHashSet<>(room.getFeatures())));
        }
        return copy;
    }

    private static Map<String, Booking> copyBookings(Map<String, Booking> source) {
        Map<String, Booking> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Booking> entry : source.entrySet()) {
            Booking booking = entry.getValue();
            TimeSlot slot = new TimeSlot(booking.getTimeSlot().getDate(),
                    booking.getTimeSlot().getStartTime(), booking.getTimeSlot().getEndTime());
            copy.put(entry.getKey(), new Booking(booking.getId(), booking.getUserId(),
                    booking.getRoomId(), slot, booking.getPurpose()));
        }
        return copy;
    }
}
