package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

import java.util.List;

/** REQ16 */
public class ListAllRoomsCommand implements Command {

    private final CROBS crobs;


    public ListAllRoomsCommand(CROBS crobs) {
        this.crobs = crobs;

    }

    @Override
    public String execute() {
        List<String> rooms = crobs.listAllRooms();
        if (rooms.isEmpty()) {
            return "No rooms found.";
        }
        return String.join(System.lineSeparator(), rooms);
    }
}
