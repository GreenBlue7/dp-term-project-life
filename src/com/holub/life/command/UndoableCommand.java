package com.holub.life.command;

/**
 * A command that can reverse itself. Only changes to the board
 * are undoable; clock controls such as Halt or Fast stay plain
 * {@link Command}s, because undoing them means nothing to the user.
 *
 * <p>Pattern: Command. A command that also supports undo.
 */
public interface UndoableCommand extends Command {
    void undo();

    /** False if execute() changed nothing, so there is nothing to undo. */
    default boolean hasEffect() {
        return true;
    }
}
