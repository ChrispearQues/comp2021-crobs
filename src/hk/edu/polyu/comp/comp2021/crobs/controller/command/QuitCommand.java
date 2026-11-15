package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;

/** REQ18 */
public class QuitCommand implements Command {

    @Override
    public String execute() {
        return "Bye.";
    }
}
