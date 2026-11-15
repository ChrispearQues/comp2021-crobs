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
}
