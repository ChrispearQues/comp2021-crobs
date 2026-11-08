package hk.edu.polyu.comp.comp2021.crobs.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** REQ1: writes one command per line, in execution order. Each run starts a fresh file. */
public class TxtLogger implements OperationLogger {

    private final Path file;
    private final BufferedWriter writer;

    public TxtLogger(Path file) {
        this.file = file;
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            // TRUNCATE_EXISTING: running the program again overwrites the old log.
            this.writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot open the TXT log " + file, e);
        }
    }

    @Override
    public void log(int index, String command) {
        try {
            writer.write(index + " " + command);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write to the TXT log " + file, e);
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot close the TXT log " + file, e);
        }
    }
}
