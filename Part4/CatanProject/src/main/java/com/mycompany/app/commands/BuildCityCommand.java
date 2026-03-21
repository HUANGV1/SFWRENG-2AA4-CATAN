package com.mycompany.app.commands;

import com.mycompany.app.BuildingCost;
import com.mycompany.app.BuildingType;
import com.mycompany.app.CatanEngine;
import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;
import com.mycompany.app.ResourceType;

import java.util.Map;

/**
 * Attempts to upgrade an existing settlement to a city for the current player.
 */
public class BuildCityCommand implements ICommand {

    private final int nodeId;
    private CatanEngine engine;
    private boolean wasBuilt;

    public BuildCityCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        if (!(controller instanceof CatanEngine)) {
            System.out.println("City building is only supported when using CatanEngine.");
            return;
        }
        CatanEngine eng = (CatanEngine) controller;
        boolean success = eng.requestBuildCity(currentPlayer.getPlayerID(), nodeId);
        if (success) {
            this.engine = eng;
            this.wasBuilt = true;
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " upgraded to a city at node " + nodeId);
        } else {
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " failed to upgrade to a city at node " + nodeId);
        }
    }

    @Override
    public void undo(IGameController controller, Player currentPlayer) {
        if (!wasBuilt || engine == null) {
            return;
        }
        engine.getBoard().getNode(nodeId).setOccupant(currentPlayer, BuildingType.SETTLEMENT);
        currentPlayer.addVictoryPoints(-1);
        Map<ResourceType, Integer> cost = BuildingCost.CITY.getCost();
        for (Map.Entry<ResourceType, Integer> e : cost.entrySet()) {
            currentPlayer.addResource(e.getKey(), e.getValue());
        }
        wasBuilt = false;
        engine.notifyObservers();
    }

    @Override
    public boolean wasSuccessful() {
        return wasBuilt;
    }
}

