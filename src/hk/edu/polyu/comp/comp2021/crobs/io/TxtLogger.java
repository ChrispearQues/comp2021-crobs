package hk.edu.polyu.comp.comp2021.crobs.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** REQ1: writes one command per line, in execution order. */
public class TxtLogger implements OperationLogger {

    private final Path file;
    private final List<String> lines = new ArrayList<>();

    /**
     * Creates the logger and empties the file.
     *
     * @param file the text file to write
     */
    public TxtLogger(Path file) {
        this.file = file;
        write();
    }

    /**
     * Appends one command.
     *
     * @param index   the position of the command in the session, starting at 1
     * @param command the text that the user typed
     */
    @Override
    public void log(int index, String command) {
        lines.add(command);
        write();
    }

    private void write() {
        StringBuilder text = new StringBuilder();
        for (String line : lines) {
            text.append(line).append('\n');
        }
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(file, text.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write the TXT log " + file, e);
        }
    }
}
