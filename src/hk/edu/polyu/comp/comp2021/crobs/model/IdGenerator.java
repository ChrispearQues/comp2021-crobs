package hk.edu.polyu.comp.comp2021.crobs.model;

/** Generates unique IDs such as U001, R001, B001. */
public class IdGenerator {

    private final String prefix;
    // Prefix letter for the ID (e.g., "U" for User, "R" for Room, "B" for Booking)
    private final int initialStart;
    // Tracks the current sequential number to be assigned next
    private int counter;

    // Creates an ID generator starting from 1 (e.g., R001)
    public IdGenerator(String prefix) {
        this(prefix,1);
    }

    // Creates an ID generator with a custom starting number
    public IdGenerator(String prefix, int start) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new CrobsException.MalformedCommandException("ID prefix cannot be empty.");
        }
        if (start < 1) {
            throw new CrobsException.MalformedCommandException("ID starting counter must be at least 1.");
        }
        this.prefix = prefix.trim();
        this.initialStart = start;
        this.counter = start;
    }
    // Formats the ID as prefix + 3-digit zero-padded number, then increments counter
    public String next() {
        return String.format("%s%03d", prefix, counter++);
    }

    // Resets the counter back to its initial value (useful for unit tests).
    public void reset() {
        this.counter = initialStart;
    }
}
