package catan.model;

import catan.player.Player;

/**
 * Represents a road segment (edge) on the Catan board.
 */
public class Edge {
    private final int edgeID;
    private Player occupant;

    public Edge(int edgeID) {
        this.edgeID = edgeID;
        this.occupant = null;
    }

    public int getEdgeID() {
        return edgeID;
    }

    public Player getOccupant() {
        return occupant;
    }

    public void setOccupant(Player player) {
        this.occupant = player;
    }

    /**
     * @return true if a road has been built on this edge
     */
    public boolean hasRoad() {
        return occupant != null;
    }

    /**
     * Build a road on this edge for the given player.
     * 
     * @return true if the road was built successfully
     */
    public boolean buildRoad(int playerID, Player player) {
        if (hasRoad()) {
            return false;
        }
        this.occupant = player;
        return true;
    }
}
