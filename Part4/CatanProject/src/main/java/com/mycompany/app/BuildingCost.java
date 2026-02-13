package com.mycompany.app;

import java.util.Map;

/**
 * Enumeration of building types with their associated costs and victory points
 * Implements Open/Closed Principle - building costs are externalized
 */
public enum BuildingCost {
	ROAD(Map.of(
		ResourceType.LUMBER, 1,
		ResourceType.BRICK, 1
	)),
	SETTLEMENT(Map.of(
		ResourceType.LUMBER, 1,
		ResourceType.BRICK, 1,
		ResourceType.GRAIN, 1,
		ResourceType.WOOL, 1
	)),
	CITY(Map.of(
		ResourceType.ORE, 3,
		ResourceType.GRAIN, 2
	));

	private final Map<ResourceType, Integer> cost;

	BuildingCost(Map<ResourceType, Integer> cost) {
		this.cost = Map.copyOf(cost);
	}

	/**
	 * Get the resource cost for this building type
	 * @return Immutable map of resource types to quantities
	 */
	public Map<ResourceType, Integer> getCost() {
		return cost;
	}

	/**
	 * Get the victory points awarded for this building
	 * @return Victory points (SETTLEMENT=1, CITY=2, ROAD=0)
	 */
	public int getVictoryPoints() {
		switch (this) {
			case SETTLEMENT: return 1;
			case CITY: return 2;  // Total, not increment
			case ROAD: return 0;
			default: return 0;
		}
	}
}
