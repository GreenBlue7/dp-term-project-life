package com.holub.life;

/**
 * Decides whether a cell is alive in the next generation. Each constant
 * is one rule, written as a plain formula.
 *
 * <p>Pattern: Strategy. Each constant is a strategy; Resident is the context.
 */
public enum LifeRule {

    /** Born with 3 neighbors, survives with 2 or 3. */
    CONWAY {
        boolean nextState(boolean alive, int neighbors) {
            return neighbors == 3 || (alive && neighbors == 2);
        }
    },

    /** Like Conway, but also born with 6 neighbors. */
    HIGHLIFE {
        boolean nextState(boolean alive, int neighbors) {
            return neighbors == 3 || neighbors == 6 || (alive && neighbors == 2);
        }
    },

    /** Born with exactly 2 neighbors. No cell ever survives. */
    SEEDS {
        boolean nextState(boolean alive, int neighbors) {
            return !alive && neighbors == 2;
        }
    },

    /** Born with 3, 6, 7 or 8 neighbors, survives with 3, 4, 6, 7 or 8. */
    DAY_AND_NIGHT {
        boolean nextState(boolean alive, int neighbors) {
            return neighbors == 3 || neighbors >= 6 || (alive && neighbors == 4);
        }
    },

    /** Born with 3 neighbors. A live cell never dies. */
    LIFE_WITHOUT_DEATH {
        boolean nextState(boolean alive, int neighbors) {
            return alive || neighbors == 3;
        }
    },

    /** Born with 3 neighbors, survives with 1 to 5. Grows into corridors. */
    MAZE {
        boolean nextState(boolean alive, int neighbors) {
            return neighbors == 3 || (alive && neighbors >= 1 && neighbors <= 5);
        }
    };

    abstract boolean nextState(boolean alive, int neighbors);
}
