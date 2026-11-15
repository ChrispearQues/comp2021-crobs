package hk.edu.polyu.comp.comp2021.crobs;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The command line of REQ1: {@code -txt} and {@code -html}. */
class ApplicationOptionsTest {

    @Test
    void noOptionMeansNoLogFiles() {
        Application.Options options = Application.parseOptions(new String[0]);

        assertNull(options.txtLog());
        assertNull(options.htmlLog());
    }

    @Test
    void readsBothFileNames() {
        Application.Options options = Application.parseOptions(
                new String[] {"-txt", "commands.txt", "-html", "commands.html"});

        assertEquals(Path.of("commands.txt"), options.txtLog());
        assertEquals(Path.of("commands.html"), options.htmlLog());
    }

    @Test
    void acceptsTheOptionsInEitherOrder() {
        Application.Options options = Application.parseOptions(
                new String[] {"-html", "commands.html", "-txt", "commands.txt"});

        assertEquals(Path.of("commands.txt"), options.txtLog());
        assertEquals(Path.of("commands.html"), options.htmlLog());
    }

    @Test
    void rejectsAMissingFileName() {
        assertThrows(IllegalArgumentException.class,
                () -> Application.parseOptions(new String[] {"-txt"}));
        assertThrows(IllegalArgumentException.class,
                () -> Application.parseOptions(new String[] {"-html"}));
        // A dangling option is an error even when the other one is complete.
        assertThrows(IllegalArgumentException.class,
                () -> Application.parseOptions(new String[] {"-txt", "commands.txt", "-html"}));
    }

    @Test
    void rejectsAnUnknownOption() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Application.parseOptions(new String[] {"-log", "x"}));
        assertEquals("Unknown option: -log", e.getMessage());
    }

    @Test
    void rejectsTheSameFileForBothLogs() {
        assertThrows(IllegalArgumentException.class, () -> Application.parseOptions(
                new String[] {"-txt", "out.log", "-html", "./out.log"}));
    }

    @Test
    void rejectsTheSameFileWrittenInTwoSpellings() {
        assertThrows(IllegalArgumentException.class, () -> Application.parseOptions(
                new String[] {"-txt", "logs/../out.log", "-html", "out.log"}));
    }
}
