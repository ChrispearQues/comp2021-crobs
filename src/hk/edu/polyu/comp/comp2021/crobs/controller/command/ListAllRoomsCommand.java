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
        return join(crobs.listAllRooms());
    }

    private static String join(java.util.List<String> lines) {
        return lines.isEmpty() ? "(no rooms)" : String.join("\n", lines);
    }
}
