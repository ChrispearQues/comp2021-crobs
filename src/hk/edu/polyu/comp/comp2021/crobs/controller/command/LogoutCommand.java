package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/** REQ6: logs out the current user. */
public class LogoutCommand implements Command {

    private final CROBS crobs;

    /**
     * Creates the command.
     *
     * @param crobs the system the command works on
     */
    public LogoutCommand(CROBS crobs) {
        this.crobs = crobs;
    }

    /** @return the confirmation line */
    @Override
    public String execute() {
        crobs.logout();
        return CrobsMessages.LOGGED_OUT;
    }
}
