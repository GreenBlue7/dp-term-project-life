package com.holub.life;

import java.util.function.BooleanSupplier;
import com.holub.life.command.UndoableCommand;

/**
 * Undoes a change that has no inverse, such as advancing a generation,
 * by restoring a snapshot taken just before it (Command + Memento). The
 * command only holds the snapshot; it never looks inside it.
 *
 * <p>The change is passed in as a function instead of being written in a
 * subclass, so one class covers both a generation step and Clear.
 */
final class SnapshotCommand implements UndoableCommand {

    private final Universe universe;
    private final BooleanSupplier change;
    private Storable before;
    private boolean changed;

    private SnapshotCommand(Universe universe, BooleanSupplier change) {
        this.universe = universe;
        this.change = change;
    }

    /** One generation. Recorded only if some cell actually changed. */
    static SnapshotCommand step(Universe universe) {
        return new SnapshotCommand(universe, universe::nextGeneration);
    }

    static SnapshotCommand clear(Universe universe) {
        return new SnapshotCommand(universe, () -> {
            universe.clear();
            return true;
        });
    }

    @Override
    public void execute() {
        before = universe.snapshot();
        changed = change.getAsBoolean();
    }

    @Override
    public void undo() {
        universe.restore(before);
    }

    @Override
    public boolean hasEffect() {
        return changed;
    }
}
