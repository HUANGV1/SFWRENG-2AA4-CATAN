package catan.model;

import catan.enums.BuildingType;
import catan.player.Player;

/**
 * Represents an intersection (vertex) on the Catan board where
 * settlements and cities can be placed.
 */
public class Node {
    private final int nodeID;
    private BuildingType type;
    private Player occupant;

    public Node(int nodeID) {
        this.nodeID = nodeID;
        this.type = BuildingType.NONE;
        this.occupant = null;
    }

    public int getNodeID() {
        return nodeID;
    }

    public BuildingType getType() {
        return type;
    }

    public Player getOccupant() {
        return occupant;
    }

    public void setOccupant(Player p, BuildingType t) {
        this.occupant = p;
        this.type = t;
    }

    public boolean isOccupied() {
        return occupant != null;
    }

    /**
     * @return true if the node is occupied by the player with the given ID
     */
    public boolean isOccupiedBy(int playerID) {
        return occupant != null && occupant.getPlayerID() == playerID;
    }

    /**
     * Place a settlement on this node for the given player.
     */
    public void buildSettlement(Player player) {
        this.occupant = player;
        this.type = BuildingType.SETTLEMENT;
    }

    /**
     * Upgrade the existing settlement to a city.
     */
    public void upgradeToCity(Player player) {
        if (this.occupant == player && this.type == BuildingType.SETTLEMENT) {
            this.type = BuildingType.CITY;
        }
    }

    /**
     * @return true if this node has a settlement belonging to the given player
     */
    public boolean hasSettlementBy(Player player) {
        return this.occupant == player && this.type == BuildingType.SETTLEMENT;
    }
}
