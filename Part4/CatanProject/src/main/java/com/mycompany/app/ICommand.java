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
}

