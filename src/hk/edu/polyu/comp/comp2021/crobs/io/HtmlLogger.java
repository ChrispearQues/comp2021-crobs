package hk.edu.polyu.comp.comp2021.crobs.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * REQ1: writes an HTML table with two columns: index and command.
 * The header is written when the logger is created, one row per command, and the
 * closing tags are written by {@link #close()}. Running the program again overwrites
 * the previous file.
 */
public class HtmlLogger implements OperationLogger {

    private static final String HEADER =
            "<html><body><table>\n<tr><th>No.</th><th>Command</th></tr>\n";
    private static final String FOOTER = "</table></body></html>\n";

    private final Path file;
    private final BufferedWriter writer;
    private boolean closed;

    public HtmlLogger(Path file) {
        this.file = file;
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            this.writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            writer.write(HEADER);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot open the HTML log " + file, e);
        }
    }

    @Override
    public void log(int index, String command) {
        try {
            writer.write("<tr><td>" + index + "</td><td>" + escape(command) + "</td></tr>\n");
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write to the HTML log " + file, e);
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            writer.write(FOOTER);
            writer.close();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot close the HTML log " + file, e);
        }
    }

    /** REQ1: a command may contain HTML metacharacters; keep the table valid. */
    static String escape(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
