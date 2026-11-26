package com.holub.life.command;

/**
 * A command that can reverse itself (Command). Only changes to the board
 * are undoable; clock controls such as Halt or Fast stay plain
 * {@link Command}s, because undoing them means nothing to the user.
 */
public interface UndoableCommand extends Command {
    void undo();

    /** False if execute() changed nothing, so there is nothing to undo. */
    default boolean hasEffect() {
        return true;
    }
}
