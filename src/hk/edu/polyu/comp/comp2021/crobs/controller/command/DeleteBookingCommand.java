package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

/** REQ12: deletes a booking of the logged-in user. */
public class DeleteBookingCommand implements Command {

    private final CROBS crobs;
    private final String bookingId;

    /**
     * Creates the command.
     *
     * @param crobs     the system the command works on
     * @param bookingId the booking to delete
     */
    public DeleteBookingCommand(CROBS crobs, String bookingId) {
        this.crobs = crobs;
        this.bookingId = bookingId;
    }

    /** @return the confirmation line */
    @Override
    public String execute() {
        crobs.deleteBooking(bookingId);
        return CrobsMessages.DONE;
    }
}
