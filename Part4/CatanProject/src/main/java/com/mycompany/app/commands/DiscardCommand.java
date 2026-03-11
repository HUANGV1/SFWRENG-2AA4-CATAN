package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;
import com.mycompany.app.ResourceType;

/**
 * Command representing a human player discarding a number of cards of
 * a specific resource type due to robber rules.
 */
public class DiscardCommand implements ICommand {

    private final int amount;
    private final ResourceType resourceType;

    public DiscardCommand(int amount, ResourceType resourceType) {
        this.amount = amount;
        this.resourceType = resourceType;
    }

    public int getAmount() {
        return amount;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        int current = currentPlayer.getResourceCount(resourceType);
        int toDrop = Math.min(amount, current);
        if (toDrop <= 0) {
            System.out.println("Player " + currentPlayer.getPlayerID()
                    + " has no " + resourceType + " to discard.");
            return;
        }
        currentPlayer.deductResource(resourceType, toDrop);
        System.out.println("Player " + currentPlayer.getPlayerID()
                + " discarded " + toDrop + " " + resourceType);
    }
}

