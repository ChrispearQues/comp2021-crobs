package hk.edu.polyu.comp.comp2021.crobs.model;

/** A date plus a start and an end time. Used for conflict detection (REQ10). */
public class TimeSlot {

    private final String date;
    private final String startTime;
    private final String endTime;

    public TimeSlot(String date, String startTime, String endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDate() { return date; }

    public String getStartTime() { return startTime; }

    public String getEndTime() { return endTime; }

    /** Two bookings conflict only if they are on the same date and overlap. */
    public boolean overlaps(TimeSlot other) {
        // TODO(REQ10): same date AND interval intersection; touching endpoints do NOT overlap
        throw new UnsupportedOperationException("TODO: TimeSlot.overlaps");
    }
}
