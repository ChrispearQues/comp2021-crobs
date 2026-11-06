package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ13 */
public class SearchCommand implements Command {

    private final CROBS crobs;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String minCapacity;
    private final String features;

    public SearchCommand(CROBS crobs, String date, String startTime, String endTime, String minCapacity, String features) {
        this.crobs = crobs;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minCapacity = minCapacity;
        this.features = features;
    }

    @Override
    public String execute() {
        // TODO(REQ13)
        throw new UnsupportedOperationException("TODO: SearchCommand.execute");
    }
}
