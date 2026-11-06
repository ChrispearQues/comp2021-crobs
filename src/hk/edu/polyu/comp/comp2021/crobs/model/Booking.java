package hk.edu.polyu.comp.comp2021.crobs.model;

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
        // TODO(REQ11)
        throw new UnsupportedOperationException("TODO: Booking.modify");
    }
}
