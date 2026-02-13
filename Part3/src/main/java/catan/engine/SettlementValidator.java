package catan.engine;

import catan.board.Board;
import catan.interfaces.IBoardGraph;
import catan.model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates settlement placement according to Catan rules.
 * Enforces the distance rule and road connectivity requirements.
 * (Single Responsibility Principle)
 */
public class SettlementValidator {
    private final Board board;

    public SettlementValidator(Board board) {
        this.board = board;
    }

    /**
     * Check if the given node is a valid location for a settlement.
     *
     * @param playerID           the player requesting the settlement
     * @param nodeID             the node where the settlement would be placed
     * @param isInitialPlacement true during setup phase (no road connection needed)
     * @return true if the placement is valid
     */
    public boolean isValid(int playerID, int nodeID, boolean isInitialPlacement) {
        Node node = board.getNode(nodeID);
        if (node == null)
            return false;

        // Node must be unoccupied
        if (node.isOccupied()) {
            return false;
        }

        // Distance rule: no adjacent node may be occupied
        if (isAdjacentNodeOccupied(playerID, nodeID)) {
            return false;
        }

        // During setup, no road connectivity required
        if (isInitialPlacement) {
            return true;
        }

        // During regular play, must be connected to player's road
        IBoardGraph topology = board.getTopology();
        for (int edgeId : topology.getAdjacentEdges(nodeID)) {
            if (board.getEdge(edgeId).getOccupant() != null
                    && board.getEdge(edgeId).getOccupant().getPlayerID() == playerID) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if any adjacent node is occupied (distance rule invariant).
     */
    private boolean isAdjacentNodeOccupied(int playerID, int nodeID) {
        IBoardGraph topology = board.getTopology();
        for (int adjNodeId : topology.getAdjacentNodes(nodeID)) {
            if (board.getNode(adjNodeId).isOccupied()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all valid locations where the player can build a settlement.
     *
     * @param playerID           the player requesting locations
     * @param isInitialPlacement true during setup phase
     * @return list of valid node IDs
     */
    public List<Integer> getValidLocations(int playerID, boolean isInitialPlacement) {
        List<Integer> validLocations = new ArrayList<>();
        for (int nodeId = 0; nodeId < 54; nodeId++) {
            if (isValid(playerID, nodeId, isInitialPlacement)) {
                validLocations.add(nodeId);
            }
        }
        return validLocations;
    }
}
