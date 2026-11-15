package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/** REQ3: removes a regular user. */
public class RemoveUserCommand implements Command {

    private final CROBS crobs;
    private final String userId;

    /**
     * Creates the command.
     *
     * @param crobs  the system the command works on
     * @param userId the ID of the user to remove
     */
    public RemoveUserCommand(CROBS crobs, String userId) {
        this.crobs = crobs;
        this.userId = userId;
    }

    /** @return the confirmation line */
    @Override
    public String execute() {
        crobs.removeUser(userId);
        return CrobsMessages.DONE;
    }
}
