package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/** REQ4: changes the access level of another user. */
public class ChangeRoleCommand implements Command {

    private final CROBS crobs;
    private final String userId;
    private final String newRole;

    /**
     * Creates the command.
     *
     * @param crobs   the system the command works on
     * @param userId  the ID of the user whose role changes
     * @param newRole "admin" or "regular"
     */
    public ChangeRoleCommand(CROBS crobs, String userId, String newRole) {
        this.crobs = crobs;
        this.userId = userId;
        this.newRole = newRole;
    }

    /** @return the confirmation line */
    @Override
    public String execute() {
        crobs.changeRole(userId, newRole);
        return CrobsMessages.DONE;
    }
}
