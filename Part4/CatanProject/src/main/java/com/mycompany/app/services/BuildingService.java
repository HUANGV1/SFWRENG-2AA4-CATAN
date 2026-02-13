package com.mycompany.app.services;

import com.mycompany.app.*;
import com.mycompany.app.validators.*;
import java.util.Map;

/**
 * Coordinates building placement operations
 * Implements Single Responsibility Principle - handles only building placement
 */
public class BuildingService {
	private Board board;
	private SettlementValidator settlementValidator;
	private RoadValidator roadValidator;

	public BuildingService(Board board,
	                      SettlementValidator settlementValidator,
	                      RoadValidator roadValidator) {
		this.board = board;
		this.settlementValidator = settlementValidator;
		this.roadValidator = roadValidator;
	}

	/**
	 * Attempt to build a settlement
	 * @param playerID The player ID
	 * @param nodeID The node location
	 * @param player The player object
	 * @return true if successful
	 */
	public boolean buildSettlement(int playerID, int nodeID, Player player) {
		// Validate location
		if (!settlementValidator.isValid(playerID, nodeID, false)) {
			return false;
		}

		// Check resources
		Map<ResourceType, Integer> cost = BuildingCost.SETTLEMENT.getCost();
		if (!player.hasResources(cost)) {
			return false;
		}

		// Build settlement
		Node node = board.getNode(nodeID);
		node.buildSettlement(player);

		// Deduct resources
		deductResources(player, cost);

		// Award victory points
		player.addVictoryPoints(BuildingCost.SETTLEMENT.getVictoryPoints());

		return true;
	}

	/**
	 * Attempt to build a road
	 * @param playerID The player ID
	 * @param edgeID The edge location
	 * @param player The player object
	 * @return true if successful
	 */
	public boolean buildRoad(int playerID, int edgeID, Player player) {
		if (!roadValidator.isValid(playerID, edgeID)) {
			return false;
		}

		Map<ResourceType, Integer> cost = BuildingCost.ROAD.getCost();
		if (!player.hasResources(cost)) {
			return false;
		}

		Edge edge = board.getEdge(edgeID);
		edge.buildRoad(player);
		deductResources(player, cost);

		return true;
	}

	/**
	 * Attempt to build a city (upgrade settlement)
	 * @param playerID The player ID
	 * @param nodeID The node location
	 * @param player The player object
	 * @return true if successful
	 */
	public boolean buildCity(int playerID, int nodeID, Player player) {
		Node node = board.getNode(nodeID);
		if (node == null) return false;

		// Must have settlement at this location
		if (!node.hasSettlementBy(playerID)) {
			return false;
		}

		Map<ResourceType, Integer> cost = BuildingCost.CITY.getCost();
		if (!player.hasResources(cost)) {
			return false;
		}

		node.upgradeToCity(player);
		deductResources(player, cost);
		player.addVictoryPoints(1); // +1 VP for upgrade (total 2 for city)

		return true;
	}

	/**
	 * Deduct resources from a player
	 * @param player The player
	 * @param cost Map of resources to deduct
	 */
	private void deductResources(Player player, Map<ResourceType, Integer> cost) {
		for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
			player.deductResource(entry.getKey(), entry.getValue());
		}
	}
}
