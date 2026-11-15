package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * REQ17: the bookings of the current user. Unlike {@link ScheduleView} this
 * never shows anybody else's booking, not even for an admin.
 */
public class BookingListView {

    private final Collection<Booking> bookings;

    /**
     * Creates a view over a snapshot of the current bookings.
     *
     * @param bookings the bookings to filter
     */
    public BookingListView(Collection<Booking> bookings) {
        this.bookings = bookings;
    }

    /**
     * Lists the bookings owned by one user.
     *
     * @param owner the logged-in user
     * @return one line per booking of {@code owner}, sorted by date then start
     *         time, or a single "no booking" line when the user has none
     */
    public List<String> of(User owner) {
        List<Booking> mine = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getUserId().equals(owner.getId())) {
                mine.add(booking);
            }
        }
        Collections.sort(mine, CompatViews.chronological());

        List<String> lines = new ArrayList<>();
        for (Booking booking : mine) {
            lines.add(CompatViews.describeWithRoom(booking));
        }
        if (lines.isEmpty()) {
            lines.add(CrobsMessages.NO_BOOKING);
        }
        return lines;
    }
}
