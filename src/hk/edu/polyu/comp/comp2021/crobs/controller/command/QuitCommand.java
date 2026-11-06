package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ18 */
public class QuitCommand implements Command {

    private final CROBS crobs;


    public QuitCommand(CROBS crobs) {
        this.crobs = crobs;

    }

    @Override
    public String execute() {
        // TODO(REQ18)
        throw new UnsupportedOperationException("TODO: QuitCommand.execute");
    }
}
