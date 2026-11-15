package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Comparator;

/** A reservation of one room by one user. */
public class Booking {

    private final String id;
    private final String userId;
    private String roomId;
    private TimeSlot timeSlot;
    private String purpose;

    /**
     * Creates a booking.
     *
     * @param id       the unique booking ID
     * @param userId   the owner, which never changes
     * @param roomId   the reserved room
     * @param timeSlot the reserved date and time
     * @param purpose  why the room is needed
     */
    public Booking(String id, String userId, String roomId, TimeSlot timeSlot, String purpose) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.timeSlot = timeSlot;
        this.purpose = purpose;
    }

    /** @return the unique booking ID */
    public String getId() { return id; }

    /** @return the owner of the booking */
    public String getUserId() { return userId; }

    /** @return the reserved room */
    public String getRoomId() { return roomId; }

    /** @return the reserved date and time */
    public TimeSlot getTimeSlot() { return timeSlot; }

    /** @return why the room is needed */
    public String getPurpose() { return purpose; }

    /**
     * REQ11: update room, date, start, end and purpose. ID and owner never change.
     *
     * @param roomId   the new room
     * @param timeSlot the new date and time
     * @param purpose  the new purpose
     */
    public void modify(String roomId, TimeSlot timeSlot, String purpose) {
        this.roomId = roomId;
        this.timeSlot = timeSlot;
        this.purpose = purpose;
    }

    /**
     * Renders the booking as it appears in the output of REQ14.
     *
     * @param userText the owner to print, possibly masked by the caller
     * @return "bookingId date startTime-endTime userText purpose"
     */
    public String describeWithUser(String userText) {
        return id + " " + timeSlot + " " + userText + " " + purpose;
    }

    /**
     * Renders the booking as it appears in the output of REQ17.
     *
     * @return "bookingId roomId date startTime-endTime purpose"
     */
    public String describeWithRoom() {
        return id + " " + roomId + " " + timeSlot + " " + purpose;
    }

    /**
     * Date, then start time, then ID. The ID is the final tie-breaker so that
     * the output of REQ14 and REQ17 is stable and reproducible.
     *
     * @return a comparator that orders bookings chronologically
     */
    public static Comparator<Booking> chronological() {
        return Comparator
                .comparing((Booking booking) -> booking.timeSlot.getDate())
                .thenComparing(booking -> booking.timeSlot.getStartTime())
                .thenComparing(booking -> booking.id);
    }
}
