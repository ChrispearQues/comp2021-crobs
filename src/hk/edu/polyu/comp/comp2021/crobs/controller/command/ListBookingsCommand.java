package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ17: shows every booking of the logged-in user. */
public class ListBookingsCommand implements Command {

    private final CROBS crobs;

    /**
     * Creates the command.
     *
     * @param crobs the system the command works on
     */
    public ListBookingsCommand(CROBS crobs) {
        this.crobs = crobs;
    }

    /** @return one line per booking, separated by newlines */
    @Override
    public String execute() {
        return String.join("\n", crobs.listBookings());
    }
}
