package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ11 */
public class ModifyBookingCommand implements Command {

    private final CROBS crobs;
    private final String bookingId;
    private final String roomId;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String purpose;

    public ModifyBookingCommand(CROBS crobs, String bookingId, String roomId, String date, String startTime, String endTime, String purpose) {
        this.crobs = crobs;
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
    }

    @Override
    public String execute() {
        // TODO(REQ11)
        throw new UnsupportedOperationException("TODO: ModifyBookingCommand.execute");
    }
}
