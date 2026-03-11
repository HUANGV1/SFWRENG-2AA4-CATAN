package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;

/**
 * Attempts to build a settlement for the current player at the specified node.
 */
public class BuildSettlementCommand implements ICommand {

    private final int nodeId;

    public BuildSettlementCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        boolean success = controller.requestBuildSettlement(currentPlayer.getPlayerID(), nodeId);
        if (success) {
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " built a settlement at node " + nodeId);
        } else {
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " failed to build a settlement at node " + nodeId);
        }
    }
}

