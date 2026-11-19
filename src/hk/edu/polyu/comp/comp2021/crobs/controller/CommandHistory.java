package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.model.CrobsMessages;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The two stacks behind undo and redo (BON2).
 *
 * <p>Only commands that changed the state are recorded. Running a new command
 * clears the redo stack, because the future that was undone no longer exists.</p>
 */
public class CommandHistory {

    private final Deque<Command> undoable = new ArrayDeque<>();
    private final Deque<Command> redoable = new ArrayDeque<>();

    /**
     * Records a command that has just run successfully.
     *
     * @param command the command to remember
     */
    public void record(Command command) {
        undoable.push(command);
        redoable.clear();
    }

    /**
     * Undoes the most recent recorded command.
     *
     * @return the text to show to the user
     */
    public String undo() {
        if (undoable.isEmpty()) {
            return CrobsMessages.NOTHING_TO_UNDO;
        }
        Command command = undoable.pop();
        command.undo();
        redoable.push(command);
        return CrobsMessages.UNDONE;
    }

    /**
     * Re-applies the command that was undone last.
     *
     * @return the text to show to the user
     */
    public String redo() {
        if (redoable.isEmpty()) {
            return CrobsMessages.NOTHING_TO_REDO;
        }
        Command command = redoable.pop();
        command.redo();
        undoable.push(command);
        return CrobsMessages.REDONE;
    }

    /** @return how many commands can still be undone */
    public int undoableCount() { return undoable.size(); }

    /** @return how many commands can still be redone */
    public int redoableCount() { return redoable.size(); }
}
