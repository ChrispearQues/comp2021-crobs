package hk.edu.polyu.comp.comp2021.crobs.controller;

/** One command object per REQ. Command objects make logging and undo/redo possible. */
public interface Command {

    /**
     * Executes the command.
     *
     * @return the text to show to the user, possibly empty
     */
    String execute();

    /**
     * Tells whether the command can be undone (BON2).
     *
     * @return true when {@link #undo()} is supported
     */
    default boolean isUndoable() {
        return false;
    }

    /**
     * Reverts the effect of {@link #execute()} (BON2).
     */
    default void undo() {
        throw new UnsupportedOperationException("This command cannot be undone");
    }
}
