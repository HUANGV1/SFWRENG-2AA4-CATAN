package catan.engine;

import catan.board.Board;
import catan.interfaces.IBoardGraph;
import catan.model.Edge;
import catan.model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates road placement according to Catan rules.
 * Ensures roads connect to the player's existing network.
 * (Single Responsibility Principle)
 */
public class RoadValidator {
    private final Board board;
    private final IBoardGraph topology;

    public RoadValidator(Board board, IBoardGraph topology) {
        this.board = board;
        this.topology = topology;
    }

    /**
     * Check if the given edge is a valid location for a road.
     *
     * @param playerID the player requesting placement
     * @param edgeID   the edge where the road would be placed
     * @return true if the placement is valid
     */
    public boolean isValid(int playerID, int edgeID) {
        Edge edge = board.getEdge(edgeID);
        if (edge == null)
            return false;

        // Edge must be unoccupied
        if (edge.hasRoad()) {
            return false;
        }

        // Must connect to player's existing structure (road or settlement)
        return connectsToPlayerStructure(playerID, edgeID);
    }

    /**
     * Check if the road at the given edge would connect to the player's
     * existing road or settlement network.
     */
    private boolean connectsToPlayerStructure(int playerID, int edgeID) {
        List<Integer> endpoints = topology.getEdgeEndpoints(edgeID);

        for (int nodeId : endpoints) {
            Node node = board.getNode(nodeId);

            // Check if player has a settlement/city at this node
            if (node.isOccupiedBy(playerID)) {
                return true;
            }

            // Check if player has an adjacent road at this node
            // (only if no opponent settlement blocks the connection)
            if (!node.isOccupied() || node.isOccupiedBy(playerID)) {
                for (int adjEdgeId : topology.getAdjacentEdges(nodeId)) {
                    if (adjEdgeId != edgeID) {
                        Edge adjEdge = board.getEdge(adjEdgeId);
                        if (adjEdge.getOccupant() != null
                                && adjEdge.getOccupant().getPlayerID() == playerID) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get all valid locations where the player can build a road.
     *
     * @param playerID the player requesting locations
     * @return list of valid edge IDs
     */
    public List<Integer> getValidLocations(int playerID) {
        List<Integer> validLocations = new ArrayList<>();
        for (int edgeId = 0; edgeId < 72; edgeId++) {
            if (isValid(playerID, edgeId)) {
                validLocations.add(edgeId);
            }
        }
        return validLocations;
    }
}
