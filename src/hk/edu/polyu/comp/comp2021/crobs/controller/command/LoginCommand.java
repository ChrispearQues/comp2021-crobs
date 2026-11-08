package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ5 */
public class LoginCommand implements Command {

    private final CROBS crobs;
    private final String userId;

    public LoginCommand(CROBS crobs, String userId) {
        this.crobs = crobs;
        this.userId = userId;
    }

    @Override
    public String execute() {
        crobs.login(userId);
        // Echo the canonical ID (the user may have typed lower case).
        return "Logged in as " + crobs.getCurrentUser().getId();
    }
}
