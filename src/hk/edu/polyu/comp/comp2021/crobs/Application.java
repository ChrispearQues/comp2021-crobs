package hk.edu.polyu.comp.comp2021.crobs;

import hk.edu.polyu.comp.comp2021.crobs.controller.CommandParser;
import hk.edu.polyu.comp.comp2021.crobs.controller.Dispatcher;
import hk.edu.polyu.comp.comp2021.crobs.io.CompositeLogger;
import hk.edu.polyu.comp.comp2021.crobs.io.HtmlLogger;
import hk.edu.polyu.comp.comp2021.crobs.io.OperationLogger;
import hk.edu.polyu.comp.comp2021.crobs.io.TxtLogger;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.view.ConsoleView;
import hk.edu.polyu.comp.comp2021.crobs.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Program entry point: reads the {@code -txt} / {@code -html} options, wires the
 * layers together and runs the REPL until {@code quit} or end of input.
 *
 * <pre>
 * java hk.edu.polyu.comp.comp2021.crobs.Application -html log.html -txt log.txt
 * </pre>
 *
 * <p>Exit status: {@code 0} on a normal run, {@code 1} when the command line is
 * wrong or when a log file cannot be opened, written or closed.</p>
 */
public class Application {

    private static final String USAGE = "Usage: java " + Application.class.getName()
            + " [-txt <command-log.txt>] [-html <command-log.html>]";

    private static final String BANNER =
            "Crobs - Campus Room Booking System. Type a command, or quit to exit.";

    /**
     * The log files requested on the command line; either one may be absent.
     * A plain class, not a record, because the project compiles at language
     * level 8 (docs/ARCHITECTURE.md section 7).
     */
    static final class Options {

        private final Path txtLog;
        private final Path htmlLog;

        private Options(Path txtLog, Path htmlLog) {
            this.txtLog = txtLog;
            this.htmlLog = htmlLog;
        }

        /** @return the file requested with {@code -txt}, or null */
        Path txtLog() {
            return txtLog;
        }

        /** @return the file requested with {@code -html}, or null */
        Path htmlLog() {
            return htmlLog;
        }
    }

    /**
     * Starts the command line interface.
     *
     * @param args "-txt &lt;file&gt;" and "-html &lt;file&gt;", both optional
     */
    public static void main(String[] args) {
        Options options;
        try {
            options = parseOptions(args);
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
            return;
        }

        OperationLogger logger;
        try {
            logger = createLoggers(options.txtLog(), options.htmlLog());
        } catch (RuntimeException e) {
            // An unreadable log file is an ordinary user error: one line, no stack trace.
            die(e.getMessage());
            return;
        }

        int status = 0;
        try {
            new Application().run(logger);
        } catch (UncheckedIOException e) {
            System.err.println("Error: " + e.getMessage());
            status = 1;
        } finally {
            try {
                logger.close();
            } catch (RuntimeException e) {
                System.err.println("Error: " + e.getMessage());
                status = 1;
            }
        }
        if (status != 0) {
            System.exit(status);
        }
    }

    /**
     * Turns the raw arguments into {@link Options}. Package-private so that the
     * command line can be tested without exiting the JVM.
     *
     * @param args the raw command line
     * @return the log files requested, with {@code null} for an absent option
     * @throws IllegalArgumentException on a missing file name, an unknown option or
     *         when the same file is given to both {@code -txt} and {@code -html}
     */
    static Options parseOptions(String[] args) {
        Path txtLog = null;
        Path htmlLog = null;

        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if ("-txt".equals(option)) {
                txtLog = fileNameAfter(args, i, option);
                i++;
            } else if ("-html".equals(option)) {
                htmlLog = fileNameAfter(args, i, option);
                i++;
            } else {
                throw new IllegalArgumentException("Unknown option: " + option);
            }
        }

        if (txtLog != null && htmlLog != null && samePath(txtLog, htmlLog)) {
            // Two writers on one file would interleave a broken table into the log.
            throw new IllegalArgumentException("The -txt and -html logs must be two different files");
        }
        return new Options(txtLog, htmlLog);
    }

    /** The file name that must follow an option, or an error when the line ends there. */
    private static Path fileNameAfter(String[] args, int optionIndex, String option) {
        if (optionIndex + 1 >= args.length) {
            throw new IllegalArgumentException("Missing file name after " + option);
        }
        return Paths.get(args[optionIndex + 1]);
    }

    /** Compares two paths after making them absolute and removing {@code .} / {@code ..}. */
    private static boolean samePath(Path first, Path second) {
        return first.toAbsolutePath().normalize().equals(second.toAbsolutePath().normalize());
    }

    /** Builds the (possibly empty) composite of the log files requested on the command line. */
    private static OperationLogger createLoggers(Path txtLog, Path htmlLog) {
        List<OperationLogger> loggers = new ArrayList<>();
        try {
            if (txtLog != null) {
                loggers.add(new TxtLogger(txtLog));
            }
            if (htmlLog != null) {
                loggers.add(new HtmlLogger(htmlLog));
            }
        } catch (RuntimeException e) {
            // Do not leak the first log file when the second one cannot be created.
            closeQuietly(loggers);
            throw e;
        }
        return new CompositeLogger(loggers.toArray(new OperationLogger[0]));
    }

    private static void closeQuietly(List<OperationLogger> loggers) {
        for (OperationLogger logger : loggers) {
            try {
                logger.close();
            } catch (RuntimeException ignored) {
                // Already unwinding: the original failure is the one worth reporting.
            }
        }
    }

    /** Wires the object graph and drives the REPL. */
    private void run(OperationLogger logger) {
        CROBS crobs = new CROBS();
        CommandParser parser = new CommandParser(crobs);
        View view = new ConsoleView();
        Dispatcher dispatcher = new Dispatcher(parser, logger, view);

        view.print(BANNER);

        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = input.readLine()) != null) {
                if (!dispatcher.dispatch(line)) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read from standard input", e);
        }
    }

    /** Prints a usage error and exits with status 1 so that scripts can detect the failure. */
    private static void fail(String message) {
        System.err.println(message);
        System.err.println(USAGE);
        System.exit(1);
    }

    /** Prints a runtime error (no usage text) and exits with status 1. */
    private static void die(String message) {
        System.err.println("Error: " + message);
        System.exit(1);
    }
}
