package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ13: lists the rooms that are free and match the request. */
public class SearchCommand implements Command {

    private final CROBS crobs;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String minCapacity;
    private final String features;

    /**
     * Creates the command.
     *
     * @param crobs       the system the command works on
     * @param date        the day, as yyyy-MM-dd
     * @param startTime   the start time, as HH:mm
     * @param endTime     the end time, as HH:mm
     * @param minCapacity the smallest acceptable capacity
     * @param features    the required features, comma separated, or "-" for none
     */
    public SearchCommand(CROBS crobs, String date, String startTime, String endTime,
                         String minCapacity, String features) {
        this.crobs = crobs;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minCapacity = minCapacity;
        this.features = features;
    }

    /** @return one line per matching room, separated by newlines */
    @Override
    public String execute() {
        return String.join("\n", crobs.search(date, startTime, endTime, minCapacity, features));
    }
}
