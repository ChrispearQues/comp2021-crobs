package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/** REQ4: changes the access level of another user. */
public class ChangeRoleCommand extends StateChangingCommand {

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
        super(crobs);
        this.userId = userId;
        this.newRole = newRole;
    }

    /** @return the confirmation line */
    @Override
    protected String run() {
        crobs.changeRole(userId, newRole);
        return CrobsMessages.DONE;
    }
}
