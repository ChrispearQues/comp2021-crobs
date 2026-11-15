package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/** REQ11: changes an existing booking of the logged-in user. */
public class ModifyBookingCommand implements Command {

    private final CROBS crobs;
    private final String bookingId;
    private final String roomId;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String purpose;

    /**
     * Creates the command.
     *
     * @param crobs     the system the command works on
     * @param bookingId the booking to change
     * @param roomId    the new room
     * @param date      the new day, as yyyy-MM-dd
     * @param startTime the new start time, as HH:mm
     * @param endTime   the new end time, as HH:mm
     * @param purpose   the new purpose
     */
    public ModifyBookingCommand(CROBS crobs, String bookingId, String roomId, String date,
                                String startTime, String endTime, String purpose) {
        this.crobs = crobs;
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
    }

    /** @return the confirmation line */
    @Override
    public String execute() {
        crobs.modifyBooking(bookingId, roomId, date, startTime, endTime, purpose);
        return CrobsMessages.DONE;
    }
}
