package hk.edu.polyu.comp.comp2021.crobs.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** REQ1: writes an HTML table with two columns: index and command. */
public class HtmlLogger implements OperationLogger {

    private static final String HEADER = "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta charset=\"UTF-8\">\n"
            + "<title>Crobs operation log</title>\n"
            + "</head>\n"
            + "<body>\n"
            + "<table border=\"1\">\n"
            + "<tr><th>Index</th><th>Command</th></tr>\n";

    private static final String FOOTER = "</table>\n</body>\n</html>\n";

    private final Path file;
    private final List<String> rows = new ArrayList<>();

    /**
     * Creates the logger and starts an empty table.
     *
     * @param file the HTML file to write
     */
    public HtmlLogger(Path file) {
        this.file = file;
        write();
    }

    /**
     * Appends one row to the table.
     *
     * @param index   the position of the command in the session, starting at 1
     * @param command the text that the user typed
     */
    @Override
    public void log(int index, String command) {
        rows.add("<tr><td>" + index + "</td><td>" + escape(command) + "</td></tr>\n");
        write();
    }

    private void write() {
        StringBuilder html = new StringBuilder(HEADER);
        for (String row : rows) {
            html.append(row);
        }
        html.append(FOOTER);
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(file, html.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write the HTML log " + file, e);
        }
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
