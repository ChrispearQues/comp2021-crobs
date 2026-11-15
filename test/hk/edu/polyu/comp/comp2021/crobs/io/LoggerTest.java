package hk.edu.polyu.comp.comp2021.crobs.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** REQ1: the TXT and HTML command logs. */
class LoggerTest {

    /** Reads a log back as text, with any platform line separator normalised to {@code \n}. */
    private static String read(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    // --- TXT -------------------------------------------------------------

    @Test
    void txtLoggerWritesOneNumberedLinePerCommand(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("commands.txt");
        TxtLogger logger = new TxtLogger(file);

        logger.log(1, "login U000");
        logger.log(2, "quit");
        logger.close();

        assertEquals("1 login U000\n2 quit\n", read(file));
    }

    @Test
    void txtLoggerOverwritesTheLogOfThePreviousRun(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("commands.txt");
        Files.writeString(file, "old run\n");

        TxtLogger logger = new TxtLogger(file);

        // Truncation happens when the file is opened, not when the first line is written.
        assertEquals(0L, Files.size(file), "the previous content must be gone as soon as the log opens");
        logger.log(1, "quit");
        logger.close();

        assertEquals("1 quit\n", read(file));
    }

    @Test
    void txtLoggerCreatesMissingParentDirectories(@TempDir Path dir) {
        Path file = dir.resolve("a/b/commands.txt");

        assertDoesNotThrow(() -> {
            TxtLogger logger = new TxtLogger(file);
            logger.log(1, "quit");
            logger.close();
        });
        assertTrue(Files.exists(file));
    }

    // --- HTML ------------------------------------------------------------

    @Test
    void htmlLoggerWritesACompleteTable(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("commands.html");
        HtmlLogger logger = new HtmlLogger(file);

        logger.log(1, "login U000");
        logger.close();

        String html = read(file);
        // The branch keeps module D's fuller document, not module A's one-line header.
        assertTrue(html.startsWith("<!DOCTYPE html>"), "html: " + html);
        assertTrue(html.contains("<table"));
        assertTrue(html.contains("<tr><th>Index</th><th>Command</th></tr>"));
        assertTrue(html.contains("<tr><td>1</td><td>login U000</td></tr>"));
        assertTrue(html.trim().endsWith("</table>\n</body>\n</html>"));
    }

    @Test
    void htmlLoggerEscapesHtmlMetacharacters(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("commands.html");
        HtmlLogger logger = new HtmlLogger(file);

        logger.log(1, "addUser \"A & B <x>\"");
        logger.close();

        String html = read(file);
        assertTrue(html.contains("A &amp; B &lt;x&gt;"));
        assertTrue(html.contains("&quot;"));
        assertTrue(!html.contains("<x>"));
    }

    @Test
    void htmlLoggerCloseIsIdempotent(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("commands.html");
        HtmlLogger logger = new HtmlLogger(file);

        logger.close();
        assertDoesNotThrow(logger::close);
        assertEquals(1, read(file).split("</table>", -1).length - 1);
    }

    // --- composite -------------------------------------------------------

    @Test
    void compositeLoggerFansOutToEveryLogger(@TempDir Path dir) throws IOException {
        Path txt = dir.resolve("commands.txt");
        Path html = dir.resolve("commands.html");
        OperationLogger logger = new CompositeLogger(new TxtLogger(txt), new HtmlLogger(html));

        logger.log(1, "quit");
        logger.close();

        assertEquals("1 quit\n", read(txt), "TXT log: " + txt);
        assertTrue(read(html).contains("<td>quit</td>"), "HTML log: " + html);
    }
}
