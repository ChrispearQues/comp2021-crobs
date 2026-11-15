package hk.edu.polyu.comp.comp2021.crobs.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * REQ13: which rooms are free for a given time slot and satisfy the desired
 * capacity and features.
 */
public class RoomSearch {

    private final Collection<Room> rooms;
    private final Collection<Booking> bookings;

    /**
     * Creates a search over a snapshot of the current rooms and bookings.
     *
     * @param rooms    the rooms to consider
     * @param bookings the bookings that make a room unavailable
     */
    public RoomSearch(Collection<Room> rooms, Collection<Booking> bookings) {
        this.rooms = rooms;
        this.bookings = bookings;
    }

    /**
     * Lists the rooms that match every criterion of REQ13.
     *
     * @param date        the day of the requested slot, as yyyy-MM-dd
     * @param startTime   the start of the requested slot, as HH:mm
     * @param endTime     the end of the requested slot, as HH:mm
     * @param minCapacity the smallest acceptable capacity
     * @param features    the required features, comma separated, or "-" for none
     * @return one line per matching room, sorted by room ID, or a single
     *         "no match" line when nothing fits
     * @throws CrobsException.MalformedCommandException for a bad date, time,
     *         interval, capacity or feature list
     */
    public List<String> search(String date, String startTime, String endTime,
                               String minCapacity, String features) {
        LocalDate day = DateFormats.parseDate(date);
        LocalTime start = DateFormats.parseTime(startTime);
        LocalTime end = DateFormats.parseTime(endTime);
        DateFormats.requireOrdered(start, end);
        int minimum = parseMinCapacity(minCapacity);
        Set<String> wanted = Room.parseFeatures(features);

        // startTime / endTime are already canonical HH:mm, so they can be used as they are.
        TimeSlot slot = new TimeSlot(day.toString(), startTime, endTime);

        List<Room> available = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getCapacity() < minimum) {
                continue;
            }
            if (!room.hasAllFeatures(wanted)) {
                continue;
            }
            if (isBusy(room, slot)) {
                continue;
            }
            available.add(room);
        }
        Collections.sort(available, Comparator.comparing((Room room) -> room.getId()));

        List<String> lines = new ArrayList<>();
        for (Room room : available) {
            lines.add(CompatViews.describe(room));
        }
        if (lines.isEmpty()) {
            lines.add(CrobsMessages.NO_MATCHING_ROOM);
        }
        return lines;
    }

    /**
     * True when an existing booking of this room overlaps the requested slot.
     *
     * <p>Compatibility note: this relies on {@link TimeSlot#overlaps(TimeSlot)},
     * which is still a stub owned by module C (REQ10). Until C lands, a search
     * that meets at least one booking fails loudly with
     * {@code UnsupportedOperationException} instead of silently returning rooms
     * that are already taken. A search over an empty booking list works today.</p>
     */
    private boolean isBusy(Room room, TimeSlot slot) {
        for (Booking booking : bookings) {
            if (booking.getRoomId().equals(room.getId()) && booking.getTimeSlot().overlaps(slot)) {
                return true;
            }
        }
        return false;
    }

    private static int parseMinCapacity(String text) {
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.INVALID_MIN_CAPACITY, text));
        }
        if (value < 0) {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.INVALID_MIN_CAPACITY, text));
        }
        return value;
    }
}
