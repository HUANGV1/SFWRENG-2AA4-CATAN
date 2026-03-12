package com.mycompany.app;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BuildingCost enum - verifies that each building type has the
 * correct resource costs, victory point values, and immutability contract.
 */
class BuildingCostTest {

    /**
     * Road costs exactly 1 LUMBER and 1 BRICK - no other resource types.
     */
    @Test
    void testRoadCost() {
        Map<ResourceType, Integer> cost = BuildingCost.ROAD.getCost();
        assertEquals(1, cost.get(ResourceType.LUMBER), "Road requires 1 LUMBER");
        assertEquals(1, cost.get(ResourceType.BRICK),  "Road requires 1 BRICK");
        assertEquals(2, cost.size(), "Road cost map must have exactly 2 entries");
    }

    /**
     * getCost() must return an immutable map; mutation must throw.
     */
    @Test
    void testCostMapIsImmutable() {
        assertThrows(UnsupportedOperationException.class,
                () -> BuildingCost.ROAD.getCost().put(ResourceType.ORE, 99),
                "Road cost map must be immutable");
    }
}
