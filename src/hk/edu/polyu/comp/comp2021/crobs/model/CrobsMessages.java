package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * Every text shown to the user, in one place, so that the CLI output, the tests
 * and the user manual can never drift apart.
 *
 * <p>Messages that contain a value are {@link String#format(String, Object...)}
 * patterns and must be filled in by the caller.</p>
 */
public final class CrobsMessages {

    /** Printed when a search does not match any room. */
    public static final String NO_MATCHING_ROOM = "No matching room.";

    /** Printed when a room has no booking in the requested week. */
    public static final String NO_BOOKING_IN_WEEK = "No booking in this week.";

    /** Printed when the current user has no booking at all. */
    public static final String NO_BOOKING = "No booking.";

    /** Rendered in place of the feature list when a room has no feature. */
    public static final String NO_FEATURES = "-";

    /** Replaces a user ID that the viewer is not allowed to see (REQ14). */
    public static final String MASKED_USER = "***";

    /** Confirmation of REQ2, filled with the generated user ID. */
    public static final String USER_CREATED = "User %s created.";

    /** Confirmation of REQ7, filled with the generated room ID. */
    public static final String ROOM_CREATED = "Room %s created.";

    /** Confirmation of REQ10, filled with the generated booking ID. */
    public static final String BOOKING_CREATED = "Booking %s created.";

    /** Confirmation of REQ5, filled with the user ID. */
    public static final String LOGGED_IN = "Logged in as %s.";

    /** Confirmation of REQ6. */
    public static final String LOGGED_OUT = "Logged out.";

    /** Confirmation of every command that changes state silently. */
    public static final String DONE = "Done.";

    /** Error for REQ14 / REQ15 / REQ16, filled with the room ID. */
    public static final String UNKNOWN_ROOM = "Error: room %s does not exist.";

    /** Error for REQ5 / REQ3 / REQ4, filled with the user ID. */
    public static final String UNKNOWN_USER = "Error: user %s does not exist.";

    /** Error for REQ11 / REQ12, filled with the booking ID. */
    public static final String UNKNOWN_BOOKING = "Error: booking %s does not exist.";

    /** Error for an unparsable date, filled with the offending text. */
    public static final String INVALID_DATE = "Error: invalid date \"%s\", expected yyyy-MM-dd.";

    /** Error for an unparsable time, filled with the offending text. */
    public static final String INVALID_TIME = "Error: invalid time \"%s\", expected HH:mm.";

    /** Error when the start time is not before the end time. */
    public static final String INVALID_INTERVAL = "Error: start time must be earlier than end time.";

    /** Error for REQ7 / REQ8, filled with the offending text. */
    public static final String INVALID_CAPACITY = "Error: capacity must be a positive integer.";

    /** Error for REQ13, filled with the offending text. */
    public static final String INVALID_MIN_CAPACITY = "Error: minCapacity must be a non-negative integer.";

    /** Error for REQ7, filled with the offending text. */
    public static final String INVALID_ROOM_TYPE = "Error: unsupported room type \"%s\".";

    /** Error for REQ8, filled with the offending field name. */
    public static final String INVALID_FIELD = "Error: unknown room field \"%s\".";

    /** Error for REQ4, filled with the offending text. */
    public static final String INVALID_ROLE = "Error: unsupported role \"%s\".";

    /** Error for REQ2 when the name is blank. */
    public static final String EMPTY_NAME = "Error: the name must not be empty.";

    /** Error for REQ10 / REQ11 when the purpose is blank. */
    public static final String EMPTY_PURPOSE = "Error: the purpose must not be empty.";

    /** Error when nobody is logged in. */
    public static final String NOT_LOGGED_IN = "Error: please login first.";

    /** Error for REQ5 when the same user logs in twice, filled with the user ID. */
    public static final String ALREADY_LOGGED_IN = "Error: %s is already logged in.";

    /** Error when a regular user tries an admin operation. */
    public static final String ADMIN_ONLY = "Error: only an admin can do that.";

    /** Error when an admin tries a superadmin operation. */
    public static final String SUPERADMIN_ONLY = "Error: only the superadmin can do that.";

    /** Error for REQ3 when the target is not a regular user. */
    public static final String REGULAR_ONLY = "Error: only a regular user can be removed.";

    /** Error for REQ4 when the target is the superadmin. */
    public static final String SUPERADMIN_FIXED = "Error: the role of the superadmin cannot be changed.";

    /** Error for REQ11 / REQ12 when the booking belongs to somebody else. */
    public static final String NOT_OWNER = "Error: you can only change your own bookings.";

    /** Error for REQ10 / REQ11, filled with room, start, end and date. */
    public static final String CONFLICT = "Error: room %s is already booked from %s to %s on %s.";

    /** Error for an unrecognised command word, filled with that word. */
    public static final String UNKNOWN_COMMAND = "Error: unknown command \"%s\".";

    /** Error for a wrong argument count, filled with the command word. */
    public static final String WRONG_ARGS = "Error: wrong number of arguments for \"%s\".";

    /** Error for an empty input line. */
    public static final String EMPTY_COMMAND = "Error: please type a command.";

    /** Error for a double quote that is never closed. */
    public static final String UNTERMINATED_QUOTE = "Error: unterminated quoted argument.";

    private CrobsMessages() { }
}
