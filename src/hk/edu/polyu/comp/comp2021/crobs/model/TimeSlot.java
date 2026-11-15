package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * A date plus a start and an end time. Used for conflict detection (REQ10).
 *
 * <p>Date and times are kept as the canonical {@code yyyy-MM-dd} / {@code HH:mm}
 * text, which makes the lexicographic order of the strings identical to the
 * chronological order.</p>
 */
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

    /**
     * Two bookings conflict only if they are on the same date and their
     * intervals intersect. Touching endpoints do not overlap, so
     * {@code 09:00-11:00} and {@code 11:00-13:00} can coexist.
     */
    public boolean overlaps(TimeSlot other) {
        if (!date.equals(other.date)) {
            return false;
        }
        return startTime.compareTo(other.endTime) < 0 && other.startTime.compareTo(endTime) < 0;
    }

    @Override
    public String toString() {
        return date + " " + startTime + "-" + endTime;
    }
}
