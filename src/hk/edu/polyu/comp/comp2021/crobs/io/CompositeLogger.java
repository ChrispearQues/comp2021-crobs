package hk.edu.polyu.comp.comp2021.crobs.io;

import java.util.Arrays;
import java.util.List;

/** Writes to several loggers at once (HTML + TXT). */
public class CompositeLogger implements OperationLogger {

    private final List<OperationLogger> loggers;

    public CompositeLogger(OperationLogger... loggers) {
        this.loggers = Arrays.asList(loggers);
    }

    @Override
    public void log(int index, String command) {
        for (OperationLogger logger : loggers) {
            logger.log(index, command);
        }
    }
}
