package com.mycompany.app.validators;

import com.mycompany.app.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates settlement placement according to Catan rules
 * Implements Single Responsibility Principle - handles only settlement validation
 */
public class SettlementValidator {
	private Board board;

	public SettlementValidator(Board board) {
		this.board = board;
	}

	/**
	 * Check if settlement can be built at this node
	 * Rules: Node empty, adjacent road owned by player, distance-2 from other buildings
	 * @param playerID The player attempting to build
	 * @param nodeID The node location
	 * @param isInitialPlacement Whether this is initial placement (skips road requirement)
	 * @return true if valid location
	 */
	public boolean isValid(int playerID, int nodeID, boolean isInitialPlacement) {
		Node node = board.getNode(nodeID);
		if (node == null) return false;

		// Node must be unoccupied
		if (node.isOccupied()) {
			return false;
		}

		// Must have adjacent road (skip for initial placement)
		if (!isInitialPlacement && !hasAdjacentRoad(playerID, nodeID)) {
			return false;
		}

		// Distance-2 rule: adjacent nodes must be empty
		int[] adjacentNodes = board.getTopology().getAdjacentNodes(nodeID);
		for (int adjNodeID : adjacentNodes) {
			Node adjNode = board.getNode(adjNodeID);
			if (adjNode != null && adjNode.getType() != BuildingType.NONE) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Check if player has an adjacent road to this node
	 */
	private boolean hasAdjacentRoad(int playerID, int nodeID) {
		int[] adjacentEdges = board.getTopology().getAdjacentEdges(nodeID);
		for (int edgeID : adjacentEdges) {
			Edge edge = board.getEdge(edgeID);
			if (edge != null && edge.getOccupant() != null &&
				edge.getOccupant().getPlayerID() == playerID) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get all valid settlement locations for a player
	 * @param playerID The player ID
	 * @param isInitialPlacement Whether this is initial placement
	 * @return Array of valid node IDs
	 */
	public int[] getValidLocations(int playerID, boolean isInitialPlacement) {
		List<Integer> validLocations = new ArrayList<>();
		for (Node node : board.getAllNodes()) {
			if (isValid(playerID, node.getNodeID(), isInitialPlacement)) {
				validLocations.add(node.getNodeID());
			}
		}
		return validLocations.stream().mapToInt(i -> i).toArray();
	}
}
