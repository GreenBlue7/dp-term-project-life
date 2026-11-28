package com.holub.life;

import com.holub.life.command.CommandHistory;

/**
 * Plays the board forward and backward like a media player. Rewinding
 * replays the undo history, because a generation cannot be computed
 * backward. What Play, Rewind and each clock tick do depends on whether
 * the player is paused, playing or rewinding.
 *
 * <p>Pattern: State. Player is the context.
 */
final class Player {

    static final int AGONIZING = 0;
    static final int SLOW = 1;
    static final int MEDIUM = 2;
    static final int FAST = 3;

    private static final int[] DELAYS = {500, 150, 70, 30};
    private static final String[] SPEED_NAMES = {"Agonizing", "Slow", "Medium", "Fast"};

    private final Clock clock;
    private final CommandHistory history;
    private final Universe universe;
    private PlayerState state = PlayerState.PAUSED;
    private int speed = SLOW;
    private Runnable onChange = () -> {};

    Player(Clock clock, CommandHistory history, Universe universe) {
        this.clock = clock;
        this.history = history;
        this.universe = universe;
        clock.addClockListener(this::tick);
    }

    // The four buttons.
    synchronized void rewind()      { state.rewind(this); }
    synchronized void play()        { state.play(this); }
    synchronized void stepBack()    { pause(); history.undo(); }
    synchronized void stepForward() { pause(); stepOnce(); }

    synchronized void playAt(int speed) {
        start(PlayerState.PLAYING, speed);
    }

    synchronized void pause() {
        state = PlayerState.PAUSED;
        clock.stop();
        onChange.run();
    }

    private synchronized void tick() {
        state.tick(this);
    }

    // Called by the states.
    void start(PlayerState next, int speed) {
        state = next;
        this.speed = speed;
        clock.startTicking(DELAYS[speed]);
        onChange.run();
    }

    void speedUp() {
        start(state, (speed + 1) % DELAYS.length);
    }

    void stepOnce() {
        history.execute(SnapshotCommand.step(universe));
    }

    /** Pauses when there is nothing left to rewind. */
    void undoOnce() {
        if (!history.undo())
            pause();
    }

    int speed() {
        return speed;
    }

    synchronized PlayerState state() {
        return state;
    }

    synchronized String status() {
        return state == PlayerState.PAUSED ? "Paused" : state + ", " + SPEED_NAMES[speed];
    }

    /** Runs after every state or speed change, e.g. to update a label. */
    void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }
}
