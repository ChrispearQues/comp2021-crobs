package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** Turns a raw input line into a Command object. Only syntax checks belong here. */
public class CommandParser {

    private final CROBS crobs;
    private final Session session;

    public CommandParser(CROBS crobs, Session session) {
        this.crobs = crobs;
        this.session = session;
    }

    /** @throws hk.edu.polyu.comp.comp2021.crobs.model.CrobsException.MalformedCommandException */
    public Command parse(String line) {
        // TODO: split on whitespace, check argument count, build the matching command
        throw new UnsupportedOperationException("TODO: CommandParser.parse");
    }
}
