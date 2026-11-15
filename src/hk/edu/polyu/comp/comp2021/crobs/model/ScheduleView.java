package hk.edu.polyu.comp.comp2021.crobs.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * REQ14: the seven-day schedule of one room.
 *
 * <p>A regular user only sees the owner of their own bookings; the others are
 * masked. Admins (and therefore the superadmin) see every owner.</p>
 */
public class ScheduleView {

    private final Map<String, Room> rooms;
    private final Collection<Booking> bookings;

    public ScheduleView(Map<String, Room> rooms, Collection<Booking> bookings) {
        this.rooms = rooms;
        this.bookings = bookings;
    }

    /**
     * @param weekStartDate first day of the window; the window is the seven
     *                      days {@code [start, start + 6]}.
     * @return one line per booking, sorted by date then start time, or a single
     *         "no booking" line when the week is empty.
     * @throws CrobsException.NotFoundException when the room does not exist.
     * @throws CrobsException.MalformedCommandException for a bad start date.
     */
    public List<String> week(String roomId, String weekStartDate, User viewer) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new CrobsException.NotFoundException(
                    String.format(CrobsMessages.UNKNOWN_ROOM, roomId));
        }
        LocalDate start = DateFormats.parseDate(weekStartDate);
        LocalDate end = DateFormats.sevenDaysFrom(start).get(6);
        boolean admin = Permission.isAdmin(viewer);

        List<Booking> inWeek = new ArrayList<>();
        for (Booking booking : bookings) {
            if (!booking.getRoomId().equals(room.getId())) {
                continue;
            }
            LocalDate day = DateFormats.parseDate(booking.getTimeSlot().getDate());
            if (day.isBefore(start) || day.isAfter(end)) {
                continue;
            }
            inWeek.add(booking);
        }
        Collections.sort(inWeek, Booking.chronological());

        List<String> lines = new ArrayList<>();
        for (Booking booking : inWeek) {
            lines.add(booking.describeWithUser(visibleOwner(booking, viewer, admin)));
        }
        if (lines.isEmpty()) {
            lines.add(CrobsMessages.NO_BOOKING_IN_WEEK);
        }
        return lines;
    }

    private static String visibleOwner(Booking booking, User viewer, boolean admin) {
        if (admin || booking.getUserId().equals(viewer.getId())) {
            return booking.getUserId();
        }
        return CrobsMessages.MASKED_USER;
    }
}
