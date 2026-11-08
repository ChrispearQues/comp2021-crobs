package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ3 */
public class RemoveUserCommand implements Command {

    private final CROBS crobs;
    private final String userId;

    public RemoveUserCommand(CROBS crobs, String userId) {
        this.crobs = crobs;
        this.userId = userId;
    }

    @Override
    public String execute() {
        crobs.removeUser(userId);
        return "User " + userId + " removed";
    }
}
