package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ7: creates a room. */
public class AddRoomCommand implements Command {

    private final CROBS crobs;
    private final String roomType;
    private final String capacity;
    private final String features;

    /**
     * Creates the command.
     *
     * @param crobs    the system the command works on
     * @param roomType one of lectureTheatre, computerLab, seminarRoom, meetingRoom
     * @param capacity the number of seats, as text
     * @param features the comma-separated features, or "-" for none
     */
    public AddRoomCommand(CROBS crobs, String roomType, String capacity, String features) {
        this.crobs = crobs;
        this.roomType = roomType;
        this.capacity = capacity;
        this.features = features;
    }

    /** @return the confirmation line, including the generated room ID */
    @Override
    public String execute() {
        return crobs.addRoom(roomType, capacity, features);
    }
}
