package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.controller.command.QuitCommand;
import hk.edu.polyu.comp.comp2021.crobs.io.OperationLogger;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import hk.edu.polyu.comp.comp2021.crobs.view.View;

/**
 * The single entry point for every command: parse -> execute -> log -> print.
 * REQ1 logging happens here and nowhere else.
 */
public class Dispatcher {

    private final CommandParser parser;
    private final OperationLogger logger;
    private final View view;
    private int counter;

    public Dispatcher(CommandParser parser, OperationLogger logger, View view) {
        this.parser = parser;
        this.logger = logger;
        this.view = view;
    }

    /** Returns false when the session should end (quit). */
    public boolean dispatch(String line) {
        if (line == null) {
            return false;
        }
        String command = line.trim();
        if (command.isEmpty()) {
            return true;
        }

        // REQ1: log the raw command before parsing, so malformed input is recorded too.
        counter++;
        logger.log(counter, command);

        try {
            Command parsed = parser.parse(command);
            String output = parsed.execute();
            if (output != null && !output.isEmpty()) {
                view.print(output);
            }
            return !(parsed instanceof QuitCommand);
        } catch (CrobsException e) {
            view.printError("Error: " + e.getMessage());
            return true;
        } catch (UnsupportedOperationException e) {
            // Modules B/C/D are still landing; keep the REPL alive instead of a stack trace.
            view.printError("Not implemented yet: " + e.getMessage());
            return true;
        }
    }

    /** Number of commands dispatched so far (REQ1 index). */
    public int getCounter() {
        return counter;
    }
}
