package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.controller.CommandHistory;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.Permission;

/** BON2: undoes the most recent state-changing command. */
public class UndoCommand implements Command {

    private final CROBS crobs;
    private final CommandHistory history;

    /**
     * Creates the command.
     *
     * @param crobs   the system the command works on
     * @param history the stack of commands that can be undone
     */
    public UndoCommand(CROBS crobs, CommandHistory history) {
        this.crobs = crobs;
        this.history = history;
    }

    /** @return "Undone." or "Nothing to undo." */
    @Override
    public String execute() {
        Permission.requireLogin(crobs.getCurrentUser());
        return history.undo();
    }
}
