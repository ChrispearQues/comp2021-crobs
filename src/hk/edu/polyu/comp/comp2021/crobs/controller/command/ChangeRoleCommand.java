package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ4 */
public class ChangeRoleCommand implements Command {

    private final CROBS crobs;
    private final String userId;
    private final String newRole;

    public ChangeRoleCommand(CROBS crobs, String userId, String newRole) {
        this.crobs = crobs;
        this.userId = userId;
        this.newRole = newRole;
    }

    @Override
    public String execute() {
        // TODO(REQ4)
        throw new UnsupportedOperationException("TODO: ChangeRoleCommand.execute");
    }
}
