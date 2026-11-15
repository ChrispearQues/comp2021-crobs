package hk.edu.polyu.comp.comp2021.crobs;

import hk.edu.polyu.comp.comp2021.crobs.controller.CommandParser;
import hk.edu.polyu.comp.comp2021.crobs.controller.Dispatcher;
import hk.edu.polyu.comp.comp2021.crobs.io.CompositeLogger;
import hk.edu.polyu.comp.comp2021.crobs.io.HtmlLogger;
import hk.edu.polyu.comp.comp2021.crobs.io.OperationLogger;
import hk.edu.polyu.comp.comp2021.crobs.io.TxtLogger;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.view.ConsoleView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Program entry point. Assembles the layers and runs the command loop.
 *
 * <pre>
 * java hk.edu.polyu.comp.comp2021.crobs.Application -html log.html -txt log.txt
 * </pre>
 */
public class Application {

    private static final String HTML_LOG = "-html";
    private static final String TXT_LOG = "-txt";

    public static void main(String[] args) {
        Path htmlFile = Paths.get("log.html");
        Path txtFile = Paths.get("log.txt");
        for (int i = 0; i + 1 < args.length; i += 2) {
            if (HTML_LOG.equals(args[i])) {
                htmlFile = Paths.get(args[i + 1]);
            } else if (TXT_LOG.equals(args[i])) {
                txtFile = Paths.get(args[i + 1]);
            }
        }

        CROBS crobs = new CROBS();
        CommandParser parser = new CommandParser(crobs);
        OperationLogger logger = new CompositeLogger(new HtmlLogger(htmlFile), new TxtLogger(txtFile));
        ConsoleView view = new ConsoleView();
        Dispatcher dispatcher = new Dispatcher(parser, logger, view);

        view.print("Crobs - Campus Room Booking System. Type a command, or quit to exit.");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = input.readLine()) != null) {
                if (!dispatcher.dispatch(line)) {
                    break;
                }
            }
        } catch (IOException e) {
            view.printError("Error: " + e.getMessage());
        }
    }
}
