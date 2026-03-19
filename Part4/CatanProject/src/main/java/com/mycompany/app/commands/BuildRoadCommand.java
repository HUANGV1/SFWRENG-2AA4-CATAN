package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;

/**
 * Attempts to build a road for the current player on the specified edge.
 *
 * In the CLI, the user specifies vertices; mapping vertices to an edge ID is
 * handled by the parser/topology. This command assumes it is given a valid
 * edge identifier.
 */
public class BuildRoadCommand implements ICommand {

    private final int edgeId;

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
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " built a road on edge " + edgeId);
        } else {
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " failed to build a road on edge " + edgeId);
        }
    }
}

