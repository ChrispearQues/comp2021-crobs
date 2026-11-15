package hk.edu.polyu.comp.comp2021.crobs.model;

/** Generates unique IDs such as U001, R001, B001. */
public class IdGenerator {

    private final String prefix;
    private int counter;

    public IdGenerator(String prefix) { this(prefix, 1); }

    public IdGenerator(String prefix, int start) {
        this.prefix = prefix;
        this.counter = start;
    }

    /** Next ID, e.g. {@code U001}, {@code U002} ... */
    public String next() {
        String id = prefix + String.format("%03d", counter);
        counter++;
        return id;
    }
}
