package hk.edu.polyu.comp.comp2021.crobs.model;

/** Generates unique IDs such as U001, R001, B001. */
public class IdGenerator {

    private static final int FIRST_NUMBER = 1;

    private final String prefix;
    private int counter;

    /**
     * Creates a generator that starts at 1.
     *
     * @param prefix the one-letter prefix of the IDs, for example "U"
     */
    public IdGenerator(String prefix) { this(prefix, FIRST_NUMBER); }

    /**
     * Creates a generator that starts at a given number.
     *
     * @param prefix the one-letter prefix of the IDs, for example "U"
     * @param start  the number of the first ID
     */
    public IdGenerator(String prefix, int start) {
        this.prefix = prefix;
        this.counter = start;
    }

    /**
     * Tells which number the next ID will carry. Used by undo and redo (BON2),
     * which restore the state of the whole system, counters included.
     *
     * @return the number that {@link #next()} will use
     */
    public int getCounter() { return counter; }

    /**
     * Puts the counter back to a number it had before. Used by undo and redo (BON2).
     *
     * @param counter the number that {@link #next()} must use next
     * @throws IllegalArgumentException when the number is smaller than 1
     */
    public void setCounter(int counter) {
        if (counter < FIRST_NUMBER) {
            throw new IllegalArgumentException("The counter must be positive.");
        }
        this.counter = counter;
    }

    /**
     * Produces the next ID, for example {@code U001}.
     *
     * @return the next unused ID
     */
    public String next() {
        String id = prefix + String.format("%03d", counter);
        counter++;
        return id;
    }
}
