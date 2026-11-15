package hk.edu.polyu.comp.comp2021.crobs.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Parsing and formatting of the two value formats used by every command:
 * dates as {@code yyyy-MM-dd} and times as {@code HH:mm}.
 *
 * <p>The parsing is strict on purpose: {@code 2026-02-29} and {@code 24:00}
 * must be rejected instead of being silently adjusted.</p>
 */
public final class DateFormats {

    /** Strict {@code yyyy-MM-dd}. {@code uuuu} (not {@code yyyy}) is required by ResolverStyle.STRICT. */
    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    /** Strict {@code HH:mm}. */
    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    /** REQ14 looks at a window of seven days. */
    private static final int DAYS_PER_WEEK = DayOfWeek.values().length;

    private DateFormats() { }

    /**
     * Parses a date.
     *
     * @param text the text to parse, expected as yyyy-MM-dd
     * @return the parsed date
     * @throws CrobsException.MalformedCommandException when the text is not a real yyyy-MM-dd date
     */
    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text, DATE);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.INVALID_DATE, text));
        }
    }

    /**
     * Parses a time.
     *
     * @param text the text to parse, expected as HH:mm
     * @return the parsed time
     * @throws CrobsException.MalformedCommandException when the text is not a real HH:mm time
     */
    public static LocalTime parseTime(String text) {
        try {
            return LocalTime.parse(text, TIME);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new CrobsException.MalformedCommandException(
                    String.format(CrobsMessages.INVALID_TIME, text));
        }
    }

    /**
     * Rejects an empty or reversed interval. Touching endpoints are allowed here;
     * whether two intervals overlap is decided by {@link TimeSlot#overlaps(TimeSlot)}.
     *
     * @param start the start time of the interval
     * @param end   the end time of the interval
     * @throws CrobsException.MalformedCommandException when start is not before end
     */
    public static void requireOrdered(LocalTime start, LocalTime end) {
        if (!start.isBefore(end)) {
            throw new CrobsException.MalformedCommandException(CrobsMessages.INVALID_INTERVAL);
        }
    }

    /**
     * The seven consecutive days starting at {@code start}, both ends included.
     *
     * @param start the first day of the week
     * @return the seven days, starting with {@code start}
     */
    public static List<LocalDate> sevenDaysFrom(LocalDate start) {
        List<LocalDate> days = new ArrayList<>(DAYS_PER_WEEK);
        for (int i = 0; i < DAYS_PER_WEEK; i++) {
            days.add(start.plusDays(i));
        }
        return days;
    }

    /**
     * The last day of the seven-day window that starts at {@code start}.
     *
     * @param start the first day of the week
     * @return the seventh day, that is {@code start} plus six days
     */
    public static LocalDate weekEnd(LocalDate start) {
        return start.plusDays(DAYS_PER_WEEK - 1);
    }
}
