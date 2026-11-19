package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/** REQ9: deletes a room. */
public class DeleteRoomCommand extends StateChangingCommand {

    private final String roomId;

    /**
     * Creates the command.
     *
     * @param crobs  the system the command works on
     * @param roomId the room to delete
     */
    public DeleteRoomCommand(CROBS crobs, String roomId) {
        super(crobs);
        this.roomId = roomId;
    }

    /** @return the confirmation line */
    @Override
    protected String run() {
        crobs.deleteRoom(roomId);
        return CrobsMessages.DONE;
    }
}
