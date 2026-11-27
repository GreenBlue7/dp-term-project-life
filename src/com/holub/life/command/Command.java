package com.holub.life.command;

/**
 * An action bound to a menu item or triggered by the clock.
 * Unlike ActionListener it does not depend on Swing, so the same
 * command can be run from a menu, the clock, or a test.
 * Stateless commands are usually written as lambdas.
 *
 * <p>Pattern: Command. This is the command interface.
 */
@FunctionalInterface
public interface Command {
    void execute();
}