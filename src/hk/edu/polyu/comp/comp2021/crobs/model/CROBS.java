package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.List;

/**
 * The single business entry point (facade). Every command goes through one of
 * these methods. All checks, state changes and formatting live here so that the
 * unit tests can reach them (line coverage is measured on this package only).
 */
public class CROBS {

    // TODO: private final Map<String, User> users / rooms / bookings
    // TODO: private final IdGenerator userIds, roomIds, bookingIds
    // TODO: the superadmin is created in the constructor

    public CROBS() {
        // TODO(overview): create the superadmin user
    }

    /** REQ5 */
    public void login(String userId) {
        throw new UnsupportedOperationException("TODO: login");
    }

    /** REQ6 */
    public void logout() {
        throw new UnsupportedOperationException("TODO: logout");
    }

    /** REQ2 */
    public String addUser(String name) {
        throw new UnsupportedOperationException("TODO: addUser");
    }

    /** REQ3 -- also cancels this user's bookings. */
    public void removeUser(String userId) {
        throw new UnsupportedOperationException("TODO: removeUser");
    }

    /** REQ4 -- superadmin only. */
    public void changeRole(String userId, String newRole) {
        throw new UnsupportedOperationException("TODO: changeRole");
    }

    /** REQ7 */
    public String addRoom(String roomType, String capacity, String features) {
        throw new UnsupportedOperationException("TODO: addRoom");
    }

    /** REQ8 */
    public void modifyRoom(String roomId, String field, String newValue) {
        throw new UnsupportedOperationException("TODO: modifyRoom");
    }

    /** REQ9 -- also cancels all future bookings of this room. */
    public void deleteRoom(String roomId) {
        throw new UnsupportedOperationException("TODO: deleteRoom");
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
