package com.holub.life;

/**
 * What the player does in each state when Play or Rewind is pressed or
 * the clock ticks. Stepping one generation back or forward works the same
 * in every state, so that stays in Player.
 *
 * <p>Pattern: State. Each constant is a concrete state.
 */
enum PlayerState {

    PAUSED {
        void play(Player player)   { player.start(PLAYING, Player.SLOW); }
        void rewind(Player player) { player.start(REWINDING, Player.SLOW); }
        void tick(Player player)   { }
    },

    PLAYING {
        void play(Player player)   { player.speedUp(); }
        void rewind(Player player) { player.start(REWINDING, player.speed()); }
        void tick(Player player)   { player.stepOnce(); }
    },

    REWINDING {
        void play(Player player)   { player.start(PLAYING, player.speed()); }
        void rewind(Player player) { player.speedUp(); }
        void tick(Player player)   { player.undoOnce(); }
    };

    abstract void play(Player player);
    abstract void rewind(Player player);
    abstract void tick(Player player);
}
