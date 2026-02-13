package catan.player;

import catan.enums.ResourceType;
import catan.interfaces.IGameController;

import java.util.EnumMap;
import java.util.Map;

/**
 * Abstract base class for all Catan players.
 * Manages resources, victory points, and defines the player action contract.
 */
public abstract class Player {
    protected final int playerID;
    protected int victoryPoints;
    protected final Map<ResourceType, Integer> resources;

    public Player(int playerID) {
        this.playerID = playerID;
        this.victoryPoints = 0;
        this.resources = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void addVictoryPoints(int points) {
        this.victoryPoints += points;
    }

    /**
     * Add resources of a specific type.
     */
    public void addResource(ResourceType type, int amount) {
        resources.put(type, resources.get(type) + amount);
    }

    /**
     * @return count of the specified resource type
     */
    public int getResourceCount(ResourceType type) {
        return resources.get(type);
    }

    /**
     * @return total number of resource cards held
     */
    public int getTotalResourceCount() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Check whether the player can afford a given cost.
     * 
     * @param cost map of resource type to amount required
     * @return true if the player has sufficient resources
     */
    public boolean hasResources(Map<ResourceType, Integer> cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            if (resources.get(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove resources of a specific type.
     * 
     * @return true if removal was successful
     */
    public boolean removeResource(ResourceType type, int amount) {
        int current = resources.get(type);
        if (current >= amount) {
            resources.put(type, current - amount);
            return true;
        }
        return false;
    }

    /**
     * Execute the player's turn using the game controller.
     */
    public abstract void takeTurn(IGameController controller);

    /**
     * Handle the robber event: discard down to half if holding more than 7 cards.
     */
    public abstract void handleOverSevenCards();

    @Override
    public String toString() {
        return "Player " + playerID;
    }
}
