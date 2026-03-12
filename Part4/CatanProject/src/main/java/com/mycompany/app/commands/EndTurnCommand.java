package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;

/**
 * Command that ends the current human player's turn.
 *
 * The actual turn loop logic lives in {@code HumanPlayer}; this command
 * exists primarily so the parser can signal an explicit end-of-turn action.
 */
public class EndTurnCommand implements ICommand {

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        // No game state change required here; HumanPlayer interprets this
        // command type as the signal to exit its input loop.
    }
}

