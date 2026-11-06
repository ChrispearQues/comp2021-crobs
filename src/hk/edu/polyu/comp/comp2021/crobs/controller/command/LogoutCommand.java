package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ6 */
public class LogoutCommand implements Command {

    private final CROBS crobs;


    public LogoutCommand(CROBS crobs) {
        this.crobs = crobs;

    }

    @Override
    public String execute() {
        // TODO(REQ6)
        throw new UnsupportedOperationException("TODO: LogoutCommand.execute");
    }
}
