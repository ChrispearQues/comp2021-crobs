package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/** REQ8 */
public class ModifyRoomCommand implements Command {

    private final CROBS crobs;
    private final String roomId;
    private final String field;
    private final String newValue;

    public ModifyRoomCommand(CROBS crobs, String roomId, String field, String newValue) {
        this.crobs = crobs;
        this.roomId = roomId;
        this.field = field;
        this.newValue = newValue;
    }

    @Override
    public String execute() {
        crobs.modifyRoom(roomId, field, newValue);
        return CrobsMessages.DONE;
    }
}
