package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ18: the dispatcher stops the session when it sees this command. */
public class QuitCommand implements Command {

    /**
     * Creates the command. The system is simply discarded when it terminates,
     * so nothing has to be released here.
     *
     * @param crobs the system the command works on
     */
    public QuitCommand(CROBS crobs) {
        // Nothing to do.
    }

    /** @return an empty text, because quitting prints nothing */
    @Override
    public String execute() {
        return "";
    }
}
