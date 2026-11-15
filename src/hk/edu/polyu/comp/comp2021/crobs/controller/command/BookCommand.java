package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ10: books a room for the logged-in user. */
public class BookCommand implements Command {

    private final CROBS crobs;
    private final String roomId;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String purpose;

    /**
     * Creates the command.
     *
     * @param crobs     the system the command works on
     * @param roomId    the room to book
     * @param date      the day, as yyyy-MM-dd
     * @param startTime the start time, as HH:mm
     * @param endTime   the end time, as HH:mm
     * @param purpose   why the room is needed
     */
    public BookCommand(CROBS crobs, String roomId, String date, String startTime,
                       String endTime, String purpose) {
        this.crobs = crobs;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
    }

    /** @return the confirmation line, including the generated booking ID */
    @Override
    public String execute() {
        return crobs.book(roomId, date, startTime, endTime, purpose);
    }
}
