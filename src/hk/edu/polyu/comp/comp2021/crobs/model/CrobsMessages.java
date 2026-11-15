package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * Every text shown to the user, in one place, so that the CLI output, the tests
 * and the user manual can never drift apart.
 *
 * <p>Messages that contain a value are {@link String#format(String, Object...)}
 * patterns and must be filled in by the caller.</p>
 */
public final class CrobsMessages {

    // ---- plain results -----------------------------------------------------

    public static final String NO_MATCHING_ROOM = "No matching room.";
    public static final String NO_BOOKING_IN_WEEK = "No booking in this week.";
    public static final String NO_BOOKING = "No booking.";

    /** Rendered in place of the feature list when a room has no feature. */
    public static final String NO_FEATURES = "-";

    /** Replaces a user ID that the viewer is not allowed to see (REQ14). */
    public static final String MASKED_USER = "***";

    // ---- results with a value ---------------------------------------------

    public static final String USER_CREATED = "User %s created.";
    public static final String ROOM_CREATED = "Room %s created.";
    public static final String BOOKING_CREATED = "Booking %s created.";
    public static final String LOGGED_IN = "Logged in as %s.";
    public static final String LOGGED_OUT = "Logged out.";
    public static final String DONE = "Done.";

    // ---- errors ------------------------------------------------------------

    public static final String UNKNOWN_ROOM = "Error: room %s does not exist.";
    public static final String UNKNOWN_USER = "Error: user %s does not exist.";
    public static final String UNKNOWN_BOOKING = "Error: booking %s does not exist.";
    public static final String INVALID_DATE = "Error: invalid date \"%s\", expected yyyy-MM-dd.";
    public static final String INVALID_TIME = "Error: invalid time \"%s\", expected HH:mm.";
    public static final String INVALID_INTERVAL = "Error: start time must be earlier than end time.";
    public static final String INVALID_CAPACITY = "Error: capacity must be a positive integer.";
    public static final String INVALID_MIN_CAPACITY = "Error: minCapacity must be a non-negative integer.";
    public static final String INVALID_ROOM_TYPE = "Error: unsupported room type \"%s\".";
    public static final String INVALID_FIELD = "Error: unknown room field \"%s\".";
    public static final String INVALID_ROLE = "Error: unsupported role \"%s\".";
    public static final String EMPTY_NAME = "Error: the name must not be empty.";
    public static final String EMPTY_PURPOSE = "Error: the purpose must not be empty.";
    public static final String NOT_LOGGED_IN = "Error: please login first.";
    public static final String ALREADY_LOGGED_IN = "Error: %s is already logged in.";
    public static final String ADMIN_ONLY = "Error: only an admin can do that.";
    public static final String SUPERADMIN_ONLY = "Error: only the superadmin can do that.";
    public static final String REGULAR_ONLY = "Error: only a regular user can be removed.";
    public static final String SUPERADMIN_FIXED = "Error: the role of the superadmin cannot be changed.";
    public static final String NOT_OWNER = "Error: you can only change your own bookings.";
    public static final String CONFLICT = "Error: room %s is already booked from %s to %s on %s.";
    public static final String UNKNOWN_COMMAND = "Error: unknown command \"%s\".";
    public static final String WRONG_ARGS = "Error: wrong number of arguments for \"%s\".";
    public static final String EMPTY_COMMAND = "Error: please type a command.";

    private CrobsMessages() { }
}
