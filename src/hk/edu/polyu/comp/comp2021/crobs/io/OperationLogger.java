package hk.edu.polyu.comp.comp2021.crobs.io;

/** REQ1: every executed command is logged. */
public interface OperationLogger {

    /**
     * Records one command.
     *
     * @param index   the position of the command in the session, starting at 1
     * @param command the text that the user typed
     */
    void log(int index, String command);

    /**
     * Releases the underlying file, if any. Safe to call more than once.
     * Implementations that write the whole file on every {@link #log} hold no
     * handle and may keep the default, empty body.
     */
    default void close() { }
}
