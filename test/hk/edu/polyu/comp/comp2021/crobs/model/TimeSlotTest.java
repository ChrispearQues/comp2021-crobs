package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeSlotTest {

    private static TimeSlot slot(String date, String start, String end) {
        return new TimeSlot(date, start, end);
    }

    @Test
    void detectsAnOverlapOnTheSameDate() {
        assertTrue(slot("2026-11-16", "09:00", "11:00").overlaps(slot("2026-11-16", "10:00", "12:00")));
        assertTrue(slot("2026-11-16", "09:00", "11:00").overlaps(slot("2026-11-16", "09:30", "09:45")));
    }

    @Test
    void treatsTouchingEndpointsAsFree() {
        assertFalse(slot("2026-11-16", "09:00", "11:00").overlaps(slot("2026-11-16", "11:00", "13:00")));
        assertFalse(slot("2026-11-16", "11:00", "13:00").overlaps(slot("2026-11-16", "09:00", "11:00")));
    }

    @Test
    void neverOverlapsOnAnotherDate() {
        assertFalse(slot("2026-11-16", "09:00", "11:00").overlaps(slot("2026-11-17", "09:00", "11:00")));
    }

    @Test
    void exposesItsValues() {
        TimeSlot slot = slot("2026-11-16", "09:00", "11:00");

        assertEquals("2026-11-16", slot.getDate());
        assertEquals("09:00", slot.getStartTime());
        assertEquals("11:00", slot.getEndTime());
        assertEquals("2026-11-16 09:00-11:00", slot.toString());
    }
}
