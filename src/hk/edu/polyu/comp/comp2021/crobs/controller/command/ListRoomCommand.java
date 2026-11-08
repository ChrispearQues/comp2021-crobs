package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ15 */
public class ListRoomCommand implements Command {

    private final CROBS crobs;
    private final String roomId;

    public ListRoomCommand(CROBS crobs, String roomId) {
        this.crobs = crobs;
        this.roomId = roomId;
    }

    @Override
    public String execute() {
        return crobs.listRoom(roomId);
    }
}
