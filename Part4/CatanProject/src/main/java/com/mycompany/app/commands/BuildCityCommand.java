package com.mycompany.app.commands;

import com.mycompany.app.CatanEngine;
import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;

/**
 * Attempts to upgrade an existing settlement to a city for the current player.
 */
public class BuildCityCommand implements ICommand {

    private final int nodeId;

    public BuildCityCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        if (!(controller instanceof CatanEngine)) {
            System.out.println("City building is only supported when using CatanEngine.");
            return;
        }
        CatanEngine engine = (CatanEngine) controller;
        boolean success = engine.requestBuildCity(currentPlayer.getPlayerID(), nodeId);
        if (success) {
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " upgraded to a city at node " + nodeId);
        } else {
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " failed to upgrade to a city at node " + nodeId);
        }
    }
}

