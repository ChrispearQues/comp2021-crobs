package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.io.OperationLogger;
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
        // TODO: increment counter, log the raw command, execute, print result,
        //       catch CrobsException and print its message
        throw new UnsupportedOperationException("TODO: Dispatcher.dispatch");
    }
}
