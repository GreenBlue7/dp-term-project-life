package com.holub.life.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The history on its own, with commands that only record what happened.
 */
class CommandHistoryTest {

    private final List<String> log = new ArrayList<>();

    @Test
    void undoesInReverseOrder() {
        CommandHistory history = new CommandHistory(10);
        history.execute(new Recorded("A", true));
        history.execute(new Recorded("B", true));

        history.undo();
        history.undo();
        assertEquals(List.of("do A", "do B", "undo B", "undo A"), log);
    }

    @Test
    void plainCommandsAreNotRecorded() {
        CommandHistory history = new CommandHistory(10);
        history.execute(() -> log.add("halt"));

        assertFalse(history.undo());
        assertEquals(List.of("halt"), log);
    }

    @Test
    void commandsWithoutEffectAreNotRecorded() {
        CommandHistory history = new CommandHistory(10);
        history.execute(new Recorded("still life", false));

        assertFalse(history.undo());
    }

    @Test
    void oldestCommandIsDroppedBeyondTheLimit() {
        CommandHistory history = new CommandHistory(2);
        history.execute(new Recorded("A", true));
        history.execute(new Recorded("B", true));
        history.execute(new Recorded("C", true));

        assertTrue(history.undo());
        assertTrue(history.undo());
        assertFalse(history.undo());
        assertEquals(List.of("do A", "do B", "do C", "undo C", "undo B"), log);
    }

    private final class Recorded implements UndoableCommand {
        private final String name;
        private final boolean effect;

        Recorded(String name, boolean effect) {
            this.name = name;
            this.effect = effect;
        }

        @Override public void execute()      { log.add("do " + name); }
        @Override public void undo()         { log.add("undo " + name); }
        @Override public boolean hasEffect() { return effect; }
    }
}
