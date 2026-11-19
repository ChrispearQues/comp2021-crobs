package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ2: creates a regular user. */
public class AddUserCommand extends StateChangingCommand {

    private final String name;

    /**
     * Creates the command.
     *
     * @param crobs the system the command works on
     * @param name  the full name of the new user
     */
    public AddUserCommand(CROBS crobs, String name) {
        super(crobs);
        this.name = name;
    }

    /** @return the confirmation line, including the generated user ID */
    @Override
    protected String run() {
        return crobs.addUser(name);
    }
}
