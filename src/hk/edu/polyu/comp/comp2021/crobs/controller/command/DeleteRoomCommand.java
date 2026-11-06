package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ9 */
public class DeleteRoomCommand implements Command {

    private final CROBS crobs;
    private final String roomId;

    public DeleteRoomCommand(CROBS crobs, String roomId) {
        this.crobs = crobs;
        this.roomId = roomId;
    }

    @Override
    public String execute() {
        // TODO(REQ9)
        throw new UnsupportedOperationException("TODO: DeleteRoomCommand.execute");
    }
}
