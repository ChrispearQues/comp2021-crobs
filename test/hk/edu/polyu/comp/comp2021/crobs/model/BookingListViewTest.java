package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingListViewTest {

    private static Booking booking(String id, String userId, String roomId,
                                   String date, String start, String end, String purpose) {
        return new Booking(id, userId, roomId, new TimeSlot(date, start, end), purpose);
    }

    private static User user(String id, Role role) {
        return new User(id, "name-" + id, role);
    }

    @Test
    void listsOnlyTheBookingsOfTheUser() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U002", "R001", "2026-11-16", "09:00", "11:00", "mine"),
                booking("B002", "U003", "R001", "2026-11-16", "11:00", "13:00", "not mine"));

        List<String> lines = new BookingListView(bookings).of(user("U002", Role.REGULAR));

        assertEquals(Collections.singletonList("B001 R001 2026-11-16 09:00-11:00 mine"), lines);
    }

    @Test
    void listsOnlyItsOwnBookingsForAnAdminToo() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U003", "R001", "2026-11-16", "09:00", "11:00", "not mine"));

        List<String> lines = new BookingListView(bookings).of(user("U002", Role.ADMIN));

        assertEquals(Collections.singletonList(CrobsMessages.NO_BOOKING), lines);
    }

    @Test
    void sortsTheBookingsByDateThenStartTime() {
        List<Booking> bookings = Arrays.asList(
                booking("B003", "U002", "R005", "2026-11-20", "10:00", "12:00", "study group"),
                booking("B001", "U002", "R002", "2026-11-16", "09:00", "11:00", "group meeting"),
                booking("B002", "U002", "R002", "2026-11-16", "14:00", "16:00", "review"));

        List<String> lines = new BookingListView(bookings).of(user("U002", Role.REGULAR));

        assertEquals(Arrays.asList(
                "B001 R002 2026-11-16 09:00-11:00 group meeting",
                "B002 R002 2026-11-16 14:00-16:00 review",
                "B003 R005 2026-11-20 10:00-12:00 study group"), lines);
    }

    @Test
    void keepsThePurposeUnchanged() {
        List<Booking> bookings = Arrays.asList(
                booking("B001", "U002", "R001", "2026-11-16", "09:00", "11:00", "COMP2021 project meeting"));

        List<String> lines = new BookingListView(bookings).of(user("U002", Role.REGULAR));

        assertEquals(Collections.singletonList(
                "B001 R001 2026-11-16 09:00-11:00 COMP2021 project meeting"), lines);
    }

    @Test
    void reportsWhenTheUserHasNoBooking() {
        List<String> lines = new BookingListView(Collections.<Booking>emptyList())
                .of(user("U002", Role.REGULAR));

        assertEquals(Collections.singletonList(CrobsMessages.NO_BOOKING), lines);
    }
}
