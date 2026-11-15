package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ7 */
public class AddRoomCommand implements Command {

    private final CROBS crobs;
    private final String roomType;
    private final String capacity;
    private final String features;

    public AddRoomCommand(CROBS crobs, String roomType, String capacity, String features) {
        this.crobs = crobs;
        this.roomType = roomType;
        this.capacity = capacity;
        this.features = features;
    }

    @Override
    public String execute() {
        return crobs.addRoom(roomType, capacity, features);
    }
}
