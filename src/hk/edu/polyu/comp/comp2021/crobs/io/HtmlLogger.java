package hk.edu.polyu.comp.comp2021.crobs.io;

import java.nio.file.Path;

/** REQ1: writes an HTML table with two columns: index and command. */
public class HtmlLogger implements OperationLogger {

    private final Path file;

    public HtmlLogger(Path file) { this.file = file; }

    @Override
    public void log(int index, String command) {
        throw new UnsupportedOperationException("TODO: append one table row");
    }
}
