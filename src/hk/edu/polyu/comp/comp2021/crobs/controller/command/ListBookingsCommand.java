package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ17 */
public class ListBookingsCommand implements Command {

    private final CROBS crobs;

    public ListBookingsCommand(CROBS crobs) {
        this.crobs = crobs;
    }

    @Override
    public String execute() {
        return String.join("\n", crobs.listBookings());
    }
}
