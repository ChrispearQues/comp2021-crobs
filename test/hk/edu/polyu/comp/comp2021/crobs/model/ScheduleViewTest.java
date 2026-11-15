package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduleViewTest {

    private static final String WEEK = "2026-11-16";

    private static Map<String, Room> rooms() {
        Map<String, Room> rooms = new LinkedHashMap<>();
        rooms.put("R001", new Room("R001", RoomType.SEMINAR_ROOM, 40, Room.parseFeatures("projector")));
        return rooms;
    }

    private static Booking booking(String id, String userId, String roomId,
                                   String date, String start, String end, String purpose) {
        return new Booking(id, userId, roomId, new TimeSlot(date, start, end), purpose);
    }

    private static User user(String id, Role role) {
        return new User(id, "name-" + id, role);
    }

    @Test
    void listsTheBookingsOfTheWholeWeekSortedByTime() {
        List<Booking> bookings = Arrays.asList(
                booking("B003", "U002", "R001", "2026-11-18", "14:00", "16:00", "consultation"),
                booking("B001", "U002", "R001", "2026-11-16", "09:00", "11:00", "group meeting"));

        List<String> lines = new ScheduleView(rooms(), bookings).week("R001", WEEK, user("U002", Role.REGULAR));

        assertEquals(Arrays.asList(
                "B001 2026-11-16 09:00-11:00 U002 group meeting",
                "B003 2026-11-18 14:00-16:00 U002 consultation"), lines);
    }

    @Test
    void includesTheFirstAndTheLastDayOfTheWindowOnly() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U002", "R001", "2026-11-16", "09:00", "11:00", "first"),
                booking("B002", "U002", "R001", "2026-11-22", "09:00", "11:00", "last"),
                booking("B003", "U002", "R001", "2026-11-23", "09:00", "11:00", "eighth day"));

        List<String> lines = new ScheduleView(rooms(), bookings).week("R001", WEEK, user("U002", Role.REGULAR));

        assertEquals(2, lines.size());
        assertEquals("B001 2026-11-16 09:00-11:00 U002 first", lines.get(0));
        assertEquals("B002 2026-11-22 09:00-11:00 U002 last", lines.get(1));
    }

    @Test
    void ignoresTheBookingsBeforeTheWindow() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U002", "R001", "2026-11-15", "09:00", "11:00", "too early"));

        List<String> lines = new ScheduleView(rooms(), bookings).week("R001", WEEK, user("U002", Role.REGULAR));

        assertEquals(Collections.singletonList(CrobsMessages.NO_BOOKING_IN_WEEK), lines);
    }

    @Test
    void showsAWindowThatCrossesTheEndOfTheMonth() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U002", "R001", "2026-12-02", "09:00", "11:00", "december"));

        List<String> lines = new ScheduleView(rooms(), bookings)
                .week("R001", "2026-11-28", user("U002", Role.REGULAR));

        assertEquals(Collections.singletonList("B001 2026-12-02 09:00-11:00 U002 december"), lines);
    }

    @Test
    void ignoresTheBookingsOfOtherRooms() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U002", "R002", "2026-11-16", "09:00", "11:00", "other room"));

        List<String> lines = new ScheduleView(rooms(), bookings).week("R001", WEEK, user("U002", Role.REGULAR));

        assertEquals(Collections.singletonList(CrobsMessages.NO_BOOKING_IN_WEEK), lines);
    }

    @Test
    void masksTheOwnerOfAnotherUsersBooking() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U003", "R001", "2026-11-16", "09:00", "11:00", "someone else"));

        List<String> lines = new ScheduleView(rooms(), bookings).week("R001", WEEK, user("U002", Role.REGULAR));

        assertEquals(Collections.singletonList("B001 2026-11-16 09:00-11:00 *** someone else"), lines);
    }

    @Test
    void showsTheOwnUserIdToARegularUser() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U002", "R001", "2026-11-16", "09:00", "11:00", "mine"));

        List<String> lines = new ScheduleView(rooms(), bookings).week("R001", WEEK, user("U002", Role.REGULAR));

        assertEquals(Collections.singletonList("B001 2026-11-16 09:00-11:00 U002 mine"), lines);
    }

    @Test
    void showsEveryOwnerToAnAdmin() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U003", "R001", "2026-11-16", "09:00", "11:00", "someone else"));

        List<String> lines = new ScheduleView(rooms(), bookings).week("R001", WEEK, user("U002", Role.ADMIN));

        assertEquals(Collections.singletonList("B001 2026-11-16 09:00-11:00 U003 someone else"), lines);
    }

    @Test
    void showsEveryOwnerToTheSuperAdmin() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U003", "R001", "2026-11-16", "09:00", "11:00", "someone else"));

        List<String> lines = new ScheduleView(rooms(), bookings)
                .week("R001", WEEK, user("U001", Role.SUPERADMIN));

        assertEquals(Collections.singletonList("B001 2026-11-16 09:00-11:00 U003 someone else"), lines);
    }

    @Test
    void rejectsAnUnknownRoom() {
        assertThrows(CrobsException.NotFoundException.class,
                () -> new ScheduleView(rooms(), Collections.<Booking>emptyList())
                        .week("R999", WEEK, user("U002", Role.REGULAR)));
    }

    @Test
    void rejectsAnInvalidWeekStart() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new ScheduleView(rooms(), Collections.<Booking>emptyList())
                        .week("R001", "16-11-2026", user("U002", Role.REGULAR)));
    }

    @Test
    void reportsAnEmptyWeek() {
        List<String> lines = new ScheduleView(rooms(), Collections.<Booking>emptyList())
                .week("R001", WEEK, user("U002", Role.REGULAR));

        assertEquals(Collections.singletonList(CrobsMessages.NO_BOOKING_IN_WEEK), lines);
    }
}
