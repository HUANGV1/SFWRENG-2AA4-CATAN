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
     * Settlement costs 1 LUMBER, 1 BRICK, 1 GRAIN, 1 WOOL - no ORE.
     */
    @Test
    void testSettlementCost() {
        Map<ResourceType, Integer> cost = BuildingCost.SETTLEMENT.getCost();
        assertEquals(1, cost.get(ResourceType.LUMBER), "Settlement requires 1 LUMBER");
        assertEquals(1, cost.get(ResourceType.BRICK),  "Settlement requires 1 BRICK");
        assertEquals(1, cost.get(ResourceType.GRAIN),  "Settlement requires 1 GRAIN");
        assertEquals(1, cost.get(ResourceType.WOOL),   "Settlement requires 1 WOOL");
        assertEquals(4, cost.size(), "Settlement cost map must have exactly 4 entries");
        assertNull(cost.get(ResourceType.ORE), "Settlement must not require ORE");
    }

    /**
     * City costs 3 ORE and 2 GRAIN - no wood, brick, or wool.
     */
    @Test
    void testCityCost() {
        Map<ResourceType, Integer> cost = BuildingCost.CITY.getCost();
        assertEquals(3, cost.get(ResourceType.ORE),   "City requires 3 ORE");
        assertEquals(2, cost.get(ResourceType.GRAIN), "City requires 2 GRAIN");
        assertEquals(2, cost.size(), "City cost map must have exactly 2 entries");
        assertNull(cost.get(ResourceType.LUMBER), "City must not require LUMBER");
        assertNull(cost.get(ResourceType.BRICK),  "City must not require BRICK");
    }

    /**
     * Roads award 0 victory points.
     */
    @Test
    void testRoadVictoryPoints() {
        assertEquals(0, BuildingCost.ROAD.getVictoryPoints(),
                "Road must award 0 VP");
    }

    /**
     * Settlements award 1 victory point.
     */
    @Test
    void testSettlementVictoryPoints() {
        assertEquals(1, BuildingCost.SETTLEMENT.getVictoryPoints(),
                "Settlement must award 1 VP");
    }

    /**
     * Cities award 2 victory points (total, representing the full city value).
     */
    @Test
    void testCityVictoryPoints() {
        assertEquals(2, BuildingCost.CITY.getVictoryPoints(),
                "City must award 2 VP");
    }

    /**
     * getCost() must return an immutable map (backed by Map.copyOf).
     * Attempts to mutate it must throw UnsupportedOperationException.
     */
    @Test
    void testCostMapIsImmutable() {
        assertThrows(UnsupportedOperationException.class,
                () -> BuildingCost.ROAD.getCost().put(ResourceType.ORE, 99),
                "Road cost map must be immutable");
        assertThrows(UnsupportedOperationException.class,
                () -> BuildingCost.SETTLEMENT.getCost().put(ResourceType.ORE, 99),
                "Settlement cost map must be immutable");
        assertThrows(UnsupportedOperationException.class,
                () -> BuildingCost.CITY.getCost().put(ResourceType.LUMBER, 99),
                "City cost map must be immutable");
    }

    /**
     * All three building types exist in the enum - no missing variants.
     */
    @Test
    void testAllThreeBuildingTypesExist() {
        assertEquals(3, BuildingCost.values().length,
                "BuildingCost must have exactly 3 variants: ROAD, SETTLEMENT, CITY");
    }

    /**
     * Player.hasResources() must return true for the exact cost of each building.
     * This integration check confirms BuildingCost costs are usable by Player logic.
     */
    @Test
    void testBuildingCostIntegrationWithPlayerHasResources() {
        Player player = new RandomAgent(0);

        // Load road cost
        for (Map.Entry<ResourceType, Integer> e : BuildingCost.ROAD.getCost().entrySet()) {
            player.addResource(e.getKey(), e.getValue());
        }
        assertTrue(player.hasResources(BuildingCost.ROAD.getCost()),
                "Player with exact road cost must pass hasResources check");

        // After deducting road cost, player cannot afford settlement (missing GRAIN + WOOL)
        for (Map.Entry<ResourceType, Integer> e : BuildingCost.ROAD.getCost().entrySet()) {
            player.deductResource(e.getKey(), e.getValue());
        }
        assertFalse(player.hasResources(BuildingCost.SETTLEMENT.getCost()),
                "Player with no resources must fail settlement hasResources check");
    }
}
