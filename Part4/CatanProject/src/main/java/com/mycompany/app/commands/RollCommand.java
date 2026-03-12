package com.mycompany.app.commands;

import com.mycompany.app.CatanEngine;
import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;

/**
 * Rolls the dice for the current player and triggers resource distribution.
 *
 * This command is optional in scenarios where the simulator already performs
 * rolling; it is provided to support interactive human-driven games.
 */
public class RollCommand implements ICommand {

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        if (!(controller instanceof CatanEngine)) {
            return;
        }
        CatanEngine engine = (CatanEngine) controller;
        int roll = engine.rollDice();
        System.out.println("Player " + currentPlayer.getPlayerID() + " rolled " + roll);
        if (roll == 7) {
            engine.handleRollSeven(currentPlayer);
        } else {
            engine.distributeResources(roll, engine.getPlayers());
        }
    }
}

