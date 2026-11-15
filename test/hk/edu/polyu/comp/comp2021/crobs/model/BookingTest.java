package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingTest {

    private static Booking booking() {
        return new Booking("B001", "U002", "R001",
                new TimeSlot("2026-11-16", "09:00", "11:00"), "group meeting");
    }

    @Test
    void keepsTheIdAndTheOwnerWhenModified() {
        Booking booking = booking();

        booking.modify("R002", new TimeSlot("2026-11-17", "14:00", "16:00"), "consultation");

        assertEquals("B001", booking.getId());
        assertEquals("U002", booking.getUserId());
        assertEquals("R002", booking.getRoomId());
        assertEquals("consultation", booking.getPurpose());
        assertEquals("2026-11-17", booking.getTimeSlot().getDate());
        assertEquals("14:00", booking.getTimeSlot().getStartTime());
        assertEquals("16:00", booking.getTimeSlot().getEndTime());
    }

    @Test
    void describesItselfForTheWeeklySchedule() {
        assertEquals("B001 2026-11-16 09:00-11:00 U002 group meeting",
                booking().describeWithUser("U002"));
    }

    @Test
    void describesItselfForTheBookingList() {
        assertEquals("B001 R001 2026-11-16 09:00-11:00 group meeting",
                booking().describeWithRoom());
    }

    @Test
    void ordersByDateThenStartTimeThenId() {
        Booking early = new Booking("B002", "U002", "R001",
                new TimeSlot("2026-11-16", "08:00", "09:00"), "a");
        Booking late = new Booking("B001", "U002", "R001",
                new TimeSlot("2026-11-16", "14:00", "16:00"), "b");
        Booking otherDay = new Booking("B003", "U002", "R001",
                new TimeSlot("2026-11-17", "08:00", "09:00"), "c");

        assertEquals(-1, Booking.chronological().compare(early, late));
        assertEquals(-1, Booking.chronological().compare(late, otherDay));
        assertEquals(1, Booking.chronological().compare(otherDay, late));
        assertEquals(0, Booking.chronological().compare(early, early));    }

    @Test
    void usesTheIdToBreakATie() {
        Booking first = new Booking("B001", "U002", "R001",
                new TimeSlot("2026-11-16", "09:00", "11:00"), "a");
        Booking second = new Booking("B002", "U002", "R001",
                new TimeSlot("2026-11-16", "09:00", "11:00"), "b");

        assertEquals(-1, Booking.chronological().compare(first, second));
    }
}
