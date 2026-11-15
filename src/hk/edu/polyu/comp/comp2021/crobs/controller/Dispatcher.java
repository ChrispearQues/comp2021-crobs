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
        if (line == null || line.trim().isEmpty()) {
            return true;
        }
        String command = line.trim();
        counter++;
        logger.log(counter, command);

        Command parsed;
        try {
            parsed = parser.parse(command);
        } catch (CrobsException e) {
            view.printError(e.getMessage());
            return true;
        }
        if (parsed instanceof QuitCommand) {
            return false;
        }
        try {
            String result = parsed.execute();
            if (result != null && !result.isEmpty()) {
                view.print(result);
            }
        } catch (CrobsException e) {
            view.printError(e.getMessage());
        }
        return true;
    }
}
