package hk.edu.polyu.comp.comp2021.crobs.io;

import java.util.Arrays;
import java.util.List;

/** Writes to several loggers at once (HTML + TXT). */
public class CompositeLogger implements OperationLogger {

    private final List<OperationLogger> loggers;

    /**
     * Creates a logger that forwards to every given logger.
     *
     * @param loggers the loggers to forward to
     */
    public CompositeLogger(OperationLogger... loggers) {
        this.loggers = Arrays.asList(loggers);
    }

    /**
     * Forwards one command to every logger.
     *
     * @param index   the position of the command in the session, starting at 1
     * @param command the text that the user typed
     */
    @Override
    public void log(int index, String command) {
        for (OperationLogger logger : loggers) {
            logger.log(index, command);
        }
    }

    /**
     * Closes every logger, even when an earlier one fails, so that no file stays
     * half-open. The first failure is reported after the others are released.
     */
    @Override
    public void close() {
        RuntimeException first = null;
        for (OperationLogger logger : loggers) {
            try {
                logger.close();
            } catch (RuntimeException e) {
                if (first == null) {
                    first = e;
                }
            }
        }
        if (first != null) {
            throw first;
        }
    }
}
