package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ16 */
public class ListAllRoomsCommand implements Command {

    private final CROBS crobs;


    public ListAllRoomsCommand(CROBS crobs) {
        this.crobs = crobs;

    }

    @Override
    public String execute() {
        // TODO(REQ16)
        throw new UnsupportedOperationException("TODO: ListAllRoomsCommand.execute");
    }
}
