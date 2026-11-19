package hk.edu.polyu.comp.comp2021.crobs.controller.command;

import hk.edu.polyu.comp.comp2021.crobs.controller.Command;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.Snapshot;

/**
 * A command that changes the state of the system, and can therefore be undone
 * (BON2).
 *
 * <p>The command takes a snapshot of the system just before it runs and one
 * just after, so undo and redo only have to put one of the two back. Working
 * with whole snapshots keeps each command small and makes undo correct even
 * when a command has side effects, such as {@code deleteRoom} cancelling the
 * future bookings of the room or {@code removeUser} deleting the bookings of
 * the user.</p>
 */
public abstract class StateChangingCommand implements Command {

    /** The system the command works on. */
    protected final CROBS crobs;

    private Snapshot before;
    private Snapshot after;

    /**
     * Creates the command.
     *
     * @param crobs the system the command works on
     */
    protected StateChangingCommand(CROBS crobs) {
        this.crobs = crobs;
    }

    /**
     * Takes the two snapshots around the work of the command.
     *
     * @return the text produced by {@link #run()}
     */
    @Override
    public final String execute() {
        before = crobs.snapshot();
        String result = run();
        after = crobs.snapshot();
        return result;
    }

    /**
     * Does the work of the command.
     *
     * @return the text to show to the user
     */
    protected abstract String run();

    /** @return always true: this command can be undone */
    @Override
    public final boolean isUndoable() {
        return true;
    }

    /** Puts the state back as it was before the command ran. */
    @Override
    public final void undo() {
        requireExecuted();
        crobs.restore(before);
    }

    /** Re-applies the effect of the command. */
    @Override
    public final void redo() {
        requireExecuted();
        crobs.restore(after);
    }

    private void requireExecuted() {
        if (before == null) {
            throw new IllegalStateException("The command has not been executed yet.");
        }
    }
}
