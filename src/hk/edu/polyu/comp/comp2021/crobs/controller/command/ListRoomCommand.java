package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ15: shows one room. */
public class ListRoomCommand implements Command {

    private final CROBS crobs;
    private final String roomId;

    /**
     * Creates the command.
     *
     * @param crobs  the system the command works on
     * @param roomId the room to show
     */
    public ListRoomCommand(CROBS crobs, String roomId) {
        this.crobs = crobs;
        this.roomId = roomId;
    }

    /** @return the line describing the room */
    @Override
    public String execute() {
        return crobs.listRoom(roomId);
    }
}
