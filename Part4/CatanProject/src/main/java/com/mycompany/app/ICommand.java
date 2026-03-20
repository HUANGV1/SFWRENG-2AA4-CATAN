package com.mycompany.app;

/**
 * Encapsulates a single human-issued game command.
 * Implementations interpret stored parameters and apply them to the game
 * via the high-level IGameController interface and the current player.
 */
public interface ICommand {

    /**
     * Execute this command against the game controller on behalf of a player.
     *
     * @param controller game controller abstraction (typically a CatanEngine)
     * @param currentPlayer the player issuing the command
     */
    void execute(IGameController controller, Player currentPlayer);

    /**
     * Undo this command. Default implementation prints a message; concrete commands
     * that support undo override this to reverse their effects.
     *
     * @param controller game controller (typically CatanEngine)
     * @param currentPlayer the player who issued the command
     */
    default void undo(IGameController controller, Player currentPlayer) {
        System.out.println("This command cannot be undone.");
    }

    /**
     * Whether this command successfully mutated state (used to decide if it should be pushed to history).
     */
    default boolean wasSuccessful() {
        return false;
    }
}

