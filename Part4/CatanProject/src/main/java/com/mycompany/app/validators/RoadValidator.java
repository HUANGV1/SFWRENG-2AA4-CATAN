package com.mycompany.app.validators;

import com.mycompany.app.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates road placement according to Catan rules
 * Implements Single Responsibility Principle - handles only road validation
 */
public class RoadValidator {
	private Board board;
	private IBoardGraph topology;

	public RoadValidator(Board board, IBoardGraph topology) {
		this.board = board;
		this.topology = topology;
	}

	/**
	 * Check if road can be built on this edge
	 * Rules: Edge empty, connects to player's road or building
	 * @param playerID The player attempting to build
	 * @param edgeID The edge location
	 * @return true if valid location
	 */
	public boolean isValid(int playerID, int edgeID) {
		Edge edge = board.getEdge(edgeID);
		if (edge == null) return false;

		// Edge must be unoccupied
		if (edge.hasRoad()) {
			return false;
		}

		// Must connect to existing road or settlement/city owned by player
		return connectsToPlayerStructure(playerID, edgeID);
	}

	/**
	 * Check if this edge connects to any of the player's structures
	 */
	private boolean connectsToPlayerStructure(int playerID, int edgeID) {
		int[] endpointNodes = topology.getEdgeEndpoints(edgeID);
		if (endpointNodes.length < 2) return false;

		for (int nodeID : endpointNodes) {
			Node node = board.getNode(nodeID);
			if (node == null) continue;

			// Check if node has player's building
			if (node.getOccupant() != null &&
				node.getOccupant().getPlayerID() == playerID &&
				node.getType() != BuildingType.NONE) {
				return true;
			}

			// Check if node has player's adjacent road
			int[] adjacentEdges = board.getTopology().getAdjacentEdges(nodeID);
			for (int adjEdgeID : adjacentEdges) {
				if (adjEdgeID == edgeID) continue;
				Edge adjEdge = board.getEdge(adjEdgeID);
				if (adjEdge != null && adjEdge.getOccupant() != null &&
					adjEdge.getOccupant().getPlayerID() == playerID) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Get all valid road locations for a player
	 * @param playerID The player ID
	 * @return Array of valid edge IDs
	 */
	public int[] getValidLocations(int playerID) {
		List<Integer> validLocations = new ArrayList<>();
		for (Edge edge : board.getAllEdges()) {
			if (isValid(playerID, edge.getEdgeID())) {
				validLocations.add(edge.getEdgeID());
			}
		}
		return validLocations.stream().mapToInt(i -> i).toArray();
	}
}
