package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/** REQ8: changes one field of a room. */
public class ModifyRoomCommand extends StateChangingCommand {

    private final String roomId;
    private final String field;
    private final String newValue;

    /**
     * Creates the command.
     *
     * @param crobs    the system the command works on
     * @param roomId   the room to change
     * @param field    "type", "capacity" or "features"
     * @param newValue the new value of that field
     */
    public ModifyRoomCommand(CROBS crobs, String roomId, String field, String newValue) {
        super(crobs);
        this.roomId = roomId;
        this.field = field;
        this.newValue = newValue;
    }

    /** @return the confirmation line */
    @Override
    protected String run() {
        crobs.modifyRoom(roomId, field, newValue);
        return CrobsMessages.DONE;
    }
}
