package catan.enums;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Resource costs for each buildable structure.
 */
public enum BuildingCost {
    ROAD(Map.of(
            ResourceType.LUMBER, 1,
            ResourceType.BRICK, 1)),
    SETTLEMENT(Map.of(
            ResourceType.LUMBER, 1,
            ResourceType.BRICK, 1,
            ResourceType.GRAIN, 1,
            ResourceType.WOOL, 1)),
    CITY(Map.of(
            ResourceType.GRAIN, 2,
            ResourceType.ORE, 3));

    private final Map<ResourceType, Integer> cost;

    BuildingCost(Map<ResourceType, Integer> cost) {
        this.cost = Collections.unmodifiableMap(new EnumMap<>(cost));
    }

    /**
     * @return unmodifiable map of resource costs
     */
    public Map<ResourceType, Integer> getCost() {
        return cost;
    }
}
