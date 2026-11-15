package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Comparator;

/** A reservation of one room by one user. */
public class Booking {

    private final String id;
    private final String userId;
    private String roomId;
    private TimeSlot timeSlot;
    private String purpose;

    public Booking(String id, String userId, String roomId, TimeSlot timeSlot, String purpose) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.timeSlot = timeSlot;
        this.purpose = purpose;
    }

    public String getId() { return id; }

    public String getUserId() { return userId; }

    public String getRoomId() { return roomId; }

    public TimeSlot getTimeSlot() { return timeSlot; }

    public String getPurpose() { return purpose; }

    /** REQ11: update room, date, start, end and purpose. ID and owner never change. */
    public void modify(String roomId, TimeSlot timeSlot, String purpose) {
        this.roomId = roomId;
        this.timeSlot = timeSlot;
        this.purpose = purpose;
    }

    /** "B003 2026-11-16 09:00-11:00 U002 group meeting" (REQ14). */
    public String describeWithUser(String userText) {
        return id + " " + timeSlot + " " + userText + " " + purpose;
    }

    /** "B003 R002 2026-11-16 09:00-11:00 group meeting" (REQ17). */
    public String describeWithRoom() {
        return id + " " + roomId + " " + timeSlot + " " + purpose;
    }

    /**
     * Date, then start time, then ID. The ID is the final tie-breaker so that
     * the output of REQ14 / REQ17 is stable and reproducible.
     */
    public static Comparator<Booking> chronological() {
        return Comparator
                .comparing((Booking booking) -> booking.timeSlot.getDate())
                .thenComparing(booking -> booking.timeSlot.getStartTime())
                .thenComparing(booking -> booking.id);
    }
}
