package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/** REQ2 */
public class AddUserCommand implements Command {

    private final CROBS crobs;
    private final String name;

    public AddUserCommand(CROBS crobs, String name) {
        this.crobs = crobs;
        this.name = name;
    }

    @Override
    public String execute() {
        return crobs.addUser(name);
    }
}
