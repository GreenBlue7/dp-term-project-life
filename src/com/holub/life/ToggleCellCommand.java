package com.holub.life;

import com.holub.life.command.UndoableCommand;

/**
 * Toggles one cell (Command). Toggling is its own inverse, so undo toggles
 * the same cell again and no snapshot is needed. The cell is kept in board
 * coordinates rather than pixels, so undo still hits the right cell after
 * the window has been resized.
 */
final class ToggleCellCommand implements UndoableCommand {

    private final Universe universe;
    private final int column;
    private final int row;

    ToggleCellCommand(Universe universe, int column, int row) {
        this.universe = universe;
        this.column = column;
        this.row = row;
    }

    @Override
    public void execute() {
        universe.toggle(column, row);
    }

    @Override
    public void undo() {
        universe.toggle(column, row);
    }
}
