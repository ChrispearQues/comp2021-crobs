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

    public String next() {
        // TODO: format as prefix + zero-padded counter, then increment
        throw new UnsupportedOperationException("TODO: IdGenerator.next");
    }
}
