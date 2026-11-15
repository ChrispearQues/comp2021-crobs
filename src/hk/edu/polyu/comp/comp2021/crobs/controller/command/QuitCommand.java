package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ18 -- the dispatcher stops the session when it sees this command. */
public class QuitCommand implements Command {

    public QuitCommand(CROBS crobs) {
        // Nothing to do: the system is simply discarded when it terminates.
    }

    @Override
    public String execute() {
        return "";
    }
}
