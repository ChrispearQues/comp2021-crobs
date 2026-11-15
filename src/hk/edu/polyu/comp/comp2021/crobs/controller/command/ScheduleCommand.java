package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ14: shows the weekly schedule of one room. */
public class ScheduleCommand implements Command {

    private final CROBS crobs;
    private final String roomId;
    private final String weekStartDate;

    /**
     * Creates the command.
     *
     * @param crobs         the system the command works on
     * @param roomId        the room whose schedule is requested
     * @param weekStartDate the first day of the week, as yyyy-MM-dd
     */
    public ScheduleCommand(CROBS crobs, String roomId, String weekStartDate) {
        this.crobs = crobs;
        this.roomId = roomId;
        this.weekStartDate = weekStartDate;
    }

    /** @return one line per booking, separated by newlines */
    @Override
    public String execute() {
        return String.join("\n", crobs.schedule(roomId, weekStartDate));
    }
}
