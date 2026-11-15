package hk.edu.polyu.comp.comp2021.crobs.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateFormatsTest {

    private static final Class<CrobsException.MalformedCommandException> MALFORMED =
            CrobsException.MalformedCommandException.class;

    @Test
    void parsesAValidDate() {
        assertEquals(LocalDate.of(2026, 11, 16), DateFormats.parseDate("2026-11-16"));
    }

    @Test
    void acceptsThe29thOfFebruaryInALeapYear() {
        assertEquals(LocalDate.of(2028, 2, 29), DateFormats.parseDate("2028-02-29"));
    }

    @Test
    void rejectsThe29thOfFebruaryInANonLeapYear() {
        assertThrows(MALFORMED, () -> DateFormats.parseDate("2026-02-29"));
    }

    @Test
    void rejectsAnImpossibleMonth() {
        assertThrows(MALFORMED, () -> DateFormats.parseDate("2026-13-01"));
    }

    @Test
    void rejectsATwoDigitYear() {
        assertThrows(MALFORMED, () -> DateFormats.parseDate("26-11-16"));
    }

    @Test
    void rejectsATextualDate() {
        assertThrows(MALFORMED, () -> DateFormats.parseDate("next monday"));
    }

    @Test
    void rejectsANullDate() {
        assertThrows(MALFORMED, () -> DateFormats.parseDate(null));
    }

    @Test
    void parsesAValidTime() {
        assertEquals(LocalTime.of(9, 30), DateFormats.parseTime("09:30"));
    }

    @Test
    void rejectsTheHour24() {
        assertThrows(MALFORMED, () -> DateFormats.parseTime("24:00"));
    }

    @Test
    void rejectsASingleDigitHour() {
        assertThrows(MALFORMED, () -> DateFormats.parseTime("9:00"));
    }

    @Test
    void rejectsATimeWithoutColon() {
        assertThrows(MALFORMED, () -> DateFormats.parseTime("0900"));
    }

    @Test
    void rejectsANullTime() {
        assertThrows(MALFORMED, () -> DateFormats.parseTime(null));
    }

    @Test
    void requiresTheStartToBeEarlierThanTheEnd() {
        assertThrows(MALFORMED, () -> DateFormats.requireOrdered(LocalTime.of(11, 0), LocalTime.of(11, 0)));
        assertThrows(MALFORMED, () -> DateFormats.requireOrdered(LocalTime.of(12, 0), LocalTime.of(11, 0)));
    }

    @Test
    void acceptsAnOrderedInterval() {
        DateFormats.requireOrdered(LocalTime.of(9, 0), LocalTime.of(11, 0));
    }

    @Test
    void sevenDaysFromCrossesTheEndOfTheMonth() {
        List<LocalDate> days = DateFormats.sevenDaysFrom(LocalDate.of(2026, 11, 28));

        assertEquals(7, days.size());
        assertEquals(LocalDate.of(2026, 11, 28), days.get(0));
        assertEquals(LocalDate.of(2026, 11, 30), days.get(2));
        assertEquals(LocalDate.of(2026, 12, 1), days.get(3));
        assertEquals(LocalDate.of(2026, 12, 4), days.get(6));
    }
}
