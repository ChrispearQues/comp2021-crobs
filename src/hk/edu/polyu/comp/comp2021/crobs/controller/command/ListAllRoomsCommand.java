package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ16: shows every room. */
public class ListAllRoomsCommand implements Command {

    private final CROBS crobs;

    /**
     * Creates the command.
     *
     * @param crobs the system the command works on
     */
    public ListAllRoomsCommand(CROBS crobs) {
        this.crobs = crobs;
    }

    /** @return one line per room, separated by newlines */
    @Override
    public String execute() {
        return String.join("\n", crobs.listAllRooms());
    }
}
