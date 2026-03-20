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
 * Attempts to build a settlement for the current player at the specified node.
 */
public class BuildSettlementCommand implements ICommand {

    private final int nodeId;
    private CatanEngine engine;
    private boolean wasBuilt;

    public BuildSettlementCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        boolean success = controller.requestBuildSettlement(currentPlayer.getPlayerID(), nodeId);
        if (success) {
            if (controller instanceof CatanEngine) {
                this.engine = (CatanEngine) controller;
                this.wasBuilt = true;
            }
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " built a settlement at node " + nodeId);
        } else {
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " failed to build a settlement at node " + nodeId);
        }
    }

    @Override
    public void undo(IGameController controller, Player currentPlayer) {
        if (!wasBuilt || engine == null) {
            return;
        }
        engine.getBoard().getNode(nodeId).setOccupant(null, BuildingType.NONE);
        currentPlayer.addVictoryPoints(-1);
        Map<ResourceType, Integer> cost = BuildingCost.SETTLEMENT.getCost();
        for (Map.Entry<ResourceType, Integer> e : cost.entrySet()) {
            currentPlayer.addResource(e.getKey(), e.getValue());
        }
        wasBuilt = false;
    }

    @Override
    public boolean wasSuccessful() {
        return wasBuilt;
    }
}

