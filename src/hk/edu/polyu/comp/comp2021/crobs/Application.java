package hk.edu.polyu.comp.comp2021.crobs;

import hk.edu.polyu.comp.comp2021.crobs.controller.CommandParser;
import hk.edu.polyu.comp.comp2021.crobs.controller.Dispatcher;
import hk.edu.polyu.comp.comp2021.crobs.controller.Session;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Program entry point: reads the {@code -txt} / {@code -html} options, wires the
 * layers together and runs the REPL until {@code quit} or end of input.
 */
public class Application {

    private static final String USAGE =
            "Usage: java " + Application.class.getName()
                    + " [-txt <command-log.txt>] [-html <command-log.html>]";

    public static void main(String[] args) {
        Path txtLog = null;
        Path htmlLog = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-txt" -> {
                    if (i + 1 >= args.length) {
                        fail("Missing file name after -txt");
                        return;
                    }
                    txtLog = Path.of(args[++i]);
                }
                case "-html" -> {
                    if (i + 1 >= args.length) {
                        fail("Missing file name after -html");
                        return;
                    }
                    htmlLog = Path.of(args[++i]);
                }
                default -> {
                    fail("Unknown option: " + args[i]);
                    return;
                }
            }
        }

        List<OperationLogger> loggers = new ArrayList<>();
        if (txtLog != null) {
            loggers.add(new TxtLogger(txtLog));
        }
        if (htmlLog != null) {
            loggers.add(new HtmlLogger(htmlLog));
        }
        OperationLogger logger = new CompositeLogger(loggers.toArray(new OperationLogger[0]));

        try {
            new Application().run(logger);
        } finally {
            logger.close();
        }
    }

    /** Wires the object graph and drives the REPL. */
    private void run(OperationLogger logger) {
        CROBS crobs = new CROBS();
        Session session = new Session(crobs);
        CommandParser parser = new CommandParser(crobs);
        View view = new ConsoleView();
        Dispatcher dispatcher = new Dispatcher(parser, logger, view);

        view.print("Crobs - campus room booking system");
        view.print(session.isLoggedIn() ? "Logged in." : "No user logged in. Type 'quit' to exit.");

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

    private static void fail(String message) {
        System.err.println(message);
        System.err.println(USAGE);
    }
}
