package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;
import com.mycompany.app.ResourceType;

/**
 * Prints basic information about the current player's state: victory
 * points and resource counts.
 */
public class StatusCommand implements ICommand {

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        System.out.println("Player " + currentPlayer.getPlayerID() + " status:");
        System.out.println("  Victory points: " + currentPlayer.getVictoryPoints());
        System.out.println("  Resources:");
        for (ResourceType type : ResourceType.values()) {
            System.out.println("    " + type + ": " + currentPlayer.getResourceCount(type));
        }
    }
}

