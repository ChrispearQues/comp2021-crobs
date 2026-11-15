package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ14 */
public class ScheduleCommand implements Command {

    private final CROBS crobs;
    private final String roomId;
    private final String weekStartDate;

    public ScheduleCommand(CROBS crobs, String roomId, String weekStartDate) {
        this.crobs = crobs;
        this.roomId = roomId;
        this.weekStartDate = weekStartDate;
    }

    @Override
    public String execute() {
        return String.join("\n", crobs.schedule(roomId, weekStartDate));
    }
}
