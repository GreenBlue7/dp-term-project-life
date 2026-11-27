package com.holub.life.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Runs commands and keeps the undoable ones, newest first, so they can be
 * undone in reverse order. Each command carries whatever it needs to
 * reverse itself.
 *
 * <p>Clock ticks arrive on the timer thread while clicks and Undo arrive
 * on the Swing thread. The methods are synchronized, so commands that go
 * through one history never change the board at the same time.
 *
 * <p>Pattern: Command. This is the invoker that supports undo.
 */
public final class CommandHistory {

    private final Deque<UndoableCommand> done = new ArrayDeque<>();
    private final int limit;

    public CommandHistory(int limit) {
        this.limit = limit;
    }

    public synchronized void execute(Command command) {
        command.execute();
        if (command instanceof UndoableCommand undoable && undoable.hasEffect()) {
            done.push(undoable);
            if (done.size() > limit)
                done.removeLast();
        }
    }

    /** @return false if there was nothing to undo. */
    public synchronized boolean undo() {
        UndoableCommand last = done.poll();
        if (last == null)
            return false;
        last.undo();
        return true;
    }

    /** Forgets every command, e.g. after a file replaced the whole board. */
    public synchronized void clear() {
        done.clear();
    }
}
