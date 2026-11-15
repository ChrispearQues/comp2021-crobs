package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ2: creates a regular user. */
public class AddUserCommand implements Command {

    private final CROBS crobs;
    private final String name;

    /**
     * Creates the command.
     *
     * @param crobs the system the command works on
     * @param name  the full name of the new user
     */
    public AddUserCommand(CROBS crobs, String name) {
        this.crobs = crobs;
        this.name = name;
    }

    /** @return the confirmation line, including the generated user ID */
    @Override
    public String execute() {
        return crobs.addUser(name);
    }
}
