package com.mycompany.app.commands;

import com.mycompany.app.BuildingCost;
import com.mycompany.app.CatanEngine;
import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;
import com.mycompany.app.ResourceType;

import java.util.Map;

/**
 * Attempts to build a road for the current player on the specified edge.
 *
 * In the CLI, the user specifies vertices; mapping vertices to an edge ID is
 * handled by the parser/topology. This command assumes it is given a valid
 * edge identifier.
 */
public class BuildRoadCommand implements ICommand {

    private final int edgeId;
    private CatanEngine engine;
    private boolean wasBuilt;

    public BuildRoadCommand(int edgeId) {
        this.edgeId = edgeId;
    }

    public int getEdgeId() {
        return edgeId;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        boolean success = controller.requestBuildRoad(currentPlayer.getPlayerID(), edgeId);
        if (success) {
            if (controller instanceof CatanEngine) {
                this.engine = (CatanEngine) controller;
                this.wasBuilt = true;
            }
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " built a road on edge " + edgeId);
        } else {
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " failed to build a road on edge " + edgeId);
        }
    }

    @Override
    public void undo(IGameController controller, Player currentPlayer) {
        if (!wasBuilt || engine == null) {
            return;
        }
        engine.getBoard().getEdge(edgeId).setOccupant(null);
        Map<ResourceType, Integer> cost = BuildingCost.ROAD.getCost();
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

