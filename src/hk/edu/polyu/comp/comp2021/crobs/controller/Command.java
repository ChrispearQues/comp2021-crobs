package hk.edu.polyu.comp.comp2021.crobs.controller;

/** One command object per REQ. Command objects make logging and undo/redo possible. */
public interface Command {

    /** Executes the command and returns the text to show to the user (may be empty). */
    String execute();

    /** BON2: only some commands can be undone. */
    default boolean isUndoable() { return false; }

    /** BON2: reverts the effect of execute(). */
    default void undo() {
        throw new UnsupportedOperationException("This command cannot be undone");
    }
}
