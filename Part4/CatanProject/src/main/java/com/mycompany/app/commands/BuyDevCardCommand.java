package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;

/**
 * Placeholder command for buying a development card.
 *
 * The current engine does not implement development card rules; this
 * command simply logs the intent so that the parser can accept the
 * corresponding input without failing.
 */
public class BuyDevCardCommand implements ICommand {

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        System.out.println("Player " + currentPlayer.getPlayerID()
                + " requested to buy a development card "
                + "(development cards not implemented).");
    }
}

