package hk.edu.polyu.comp.comp2021.crobs.io;

import java.nio.file.Path;

/** REQ1: writes one command per line, in execution order. */
public class TxtLogger implements OperationLogger {

    private final Path file;

    public TxtLogger(Path file) { this.file = file; }

    @Override
    public void log(int index, String command) {
        throw new UnsupportedOperationException("TODO: append one line");
    }
}
