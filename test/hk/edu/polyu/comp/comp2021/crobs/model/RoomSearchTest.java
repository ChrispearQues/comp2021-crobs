package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomSearchTest {

    private static final String DATE = "2026-11-16";

    private static Room room(String id, int capacity, String features) {
        return new Room(id, RoomType.SEMINAR_ROOM, capacity, Room.parseFeatures(features));
    }

    private static Booking booking(String id, String roomId, String date, String start, String end) {
        return new Booking(id, "U002", roomId, new TimeSlot(date, start, end), "purpose");
    }

    private static List<String> search(Collection<Room> rooms, Collection<Booking> bookings,
                                       String date, String start, String end,
                                       String minCapacity, String features) {
        return new RoomSearch(rooms, bookings).search(date, start, end, minCapacity, features);
    }

    @Test
    void includesARoomWhoseCapacityEqualsTheMinimum() {
        List<String> lines = search(Arrays.asList(room("R001", 30, "-")), Collections.<Booking>emptyList(),
                DATE, "09:00", "11:00", "30", "-");

        assertEquals(Collections.singletonList("R001 seminarRoom 30 -"), lines);
    }

    @Test
    void excludesARoomBelowTheMinimumCapacity() {
        List<String> lines = search(Arrays.asList(room("R001", 20, "-")), Collections.<Booking>emptyList(),
                DATE, "09:00", "11:00", "30", "-");

        assertEquals(Collections.singletonList(CrobsMessages.NO_MATCHING_ROOM), lines);
    }

    @Test
    void requiresEveryRequestedFeature() {
        List<Room> rooms = Arrays.asList(room("R001", 40, "projector,whiteboard"),
                room("R002", 40, "projector"));

        List<String> lines = search(rooms, Collections.<Booking>emptyList(),
                DATE, "09:00", "11:00", "10", "projector,whiteboard");

        assertEquals(Collections.singletonList("R001 seminarRoom 40 projector,whiteboard"), lines);
    }

    @Test
    void treatsADashAsNoFeatureRequirement() {
        List<String> lines = search(Arrays.asList(room("R001", 40, "projector")),
                Collections.<Booking>emptyList(), DATE, "09:00", "11:00", "0", "-");

        assertEquals(Collections.singletonList("R001 seminarRoom 40 projector"), lines);
    }

    @Test
    void ignoresTheCaseOfTheRequestedFeatures() {
        List<String> lines = search(Arrays.asList(room("R001", 40, "projector")),
                Collections.<Booking>emptyList(), DATE, "09:00", "11:00", "1", "PROJECTOR");

        assertEquals(Collections.singletonList("R001 seminarRoom 40 projector"), lines);
    }

    @Test
    void excludesARoomThatIsBookedDuringTheSlot() {
        List<Booking> bookings = Arrays.asList(booking("B001", "R001", DATE, "10:00", "12:00"));

        List<String> lines = search(Arrays.asList(room("R001", 40, "-")), bookings,
                DATE, "09:00", "11:00", "1", "-");

        assertEquals(Collections.singletonList(CrobsMessages.NO_MATCHING_ROOM), lines);
    }

    @Test
    void keepsARoomFreeWhenTheBookingOnlyTouchesTheSlot() {
        List<Booking> bookings = Arrays.asList(booking("B001", "R001", DATE, "11:00", "13:00"));

        List<String> lines = search(Arrays.asList(room("R001", 40, "-")), bookings,
                DATE, "09:00", "11:00", "1", "-");

        assertEquals(Collections.singletonList("R001 seminarRoom 40 -"), lines);
    }

    @Test
    void keepsARoomFreeWhenTheBookingIsOnAnotherDay() {
        List<Booking> bookings = Arrays.asList(booking("B001", "R001", "2026-11-17", "09:00", "11:00"));

        List<String> lines = search(Arrays.asList(room("R001", 40, "-")), bookings,
                DATE, "09:00", "11:00", "1", "-");

        assertEquals(Collections.singletonList("R001 seminarRoom 40 -"), lines);
    }

    @Test
    void sortsTheMatchesByRoomId() {
        List<Room> rooms = new ArrayList<>(Arrays.asList(room("R003", 40, "-"), room("R001", 40, "-")));

        List<String> lines = search(rooms, Collections.<Booking>emptyList(),
                DATE, "09:00", "11:00", "1", "-");

        assertEquals(Arrays.asList("R001 seminarRoom 40 -", "R003 seminarRoom 40 -"), lines);
    }

    @Test
    void reportsWhenNothingMatches() {
        List<String> lines = search(Collections.<Room>emptyList(), Collections.<Booking>emptyList(),
                DATE, "09:00", "11:00", "1", "-");

        assertEquals(Collections.singletonList(CrobsMessages.NO_MATCHING_ROOM), lines);
    }

    @Test
    void rejectsAnInvalidDate() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> search(Collections.<Room>emptyList(), Collections.<Booking>emptyList(),
                        "2026-13-01", "09:00", "11:00", "1", "-"));
    }

    @Test
    void rejectsAnInvalidTime() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> search(Collections.<Room>emptyList(), Collections.<Booking>emptyList(),
                        DATE, "9:00", "11:00", "1", "-"));
    }

    @Test
    void rejectsAReversedInterval() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> search(Collections.<Room>emptyList(), Collections.<Booking>emptyList(),
                        DATE, "11:00", "11:00", "1", "-"));
    }

    @Test
    void rejectsANonNumericCapacity() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> search(Collections.<Room>emptyList(), Collections.<Booking>emptyList(),
                        DATE, "09:00", "11:00", "many", "-"));
    }

    @Test
    void rejectsANegativeCapacity() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> search(Collections.<Room>emptyList(), Collections.<Booking>emptyList(),
                        DATE, "09:00", "11:00", "-5", "-"));
    }
}
