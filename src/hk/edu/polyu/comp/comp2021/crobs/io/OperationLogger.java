package hk.edu.polyu.comp.comp2021.crobs.io;

/** REQ1: every executed command is logged. */
public interface OperationLogger {

    void log(int index, String command);

    /** Flushes and closes the underlying file, if any. Safe to call more than once. */
    default void close() { }
}
