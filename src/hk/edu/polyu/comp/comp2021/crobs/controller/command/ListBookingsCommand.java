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
        return join(crobs.listBookings());
    }

    private static String join(java.util.List<String> lines) {
        return lines.isEmpty() ? "(no bookings)" : String.join("\n", lines);
    }
}
