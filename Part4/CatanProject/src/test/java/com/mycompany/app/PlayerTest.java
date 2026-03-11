package com.mycompany.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        // Using RandomAgent as a concrete class to test Player's methods
        player = new RandomAgent(1);
    }

    @Test
    void testInitialState() {
        assertEquals(1, player.getPlayerID());
        assertEquals(0, player.getVictoryPoints());
        assertEquals(0, player.getTotalResourceCards());
    }

    @Test
    void testAddVictoryPoints() {
        player.addVictoryPoints(2);
        assertEquals(2, player.getVictoryPoints());
        player.addVictoryPoints(1);
        assertEquals(3, player.getVictoryPoints());
    }

    @Test
    void testAddResource() {
        player.addResource(ResourceType.LUMBER, 3);
        assertEquals(3, player.getResourceCount(ResourceType.LUMBER));
        assertEquals(3, player.getTotalResourceCards());

        player.addResource(ResourceType.BRICK, 2);
        assertEquals(2, player.getResourceCount(ResourceType.BRICK));
        assertEquals(5, player.getTotalResourceCards());
    }

    /**
     * Boundary testing for deductResource.
     * Tests deducting exactly the amount held, deducting less than held,
     * and deducting more than held (bounded at 0).
     */
    @Test
    void testDeductResourceBoundary() {
        player.addResource(ResourceType.ORE, 5);

        // Exact deduction (boundary)
        player.deductResource(ResourceType.ORE, 5);
        assertEquals(0, player.getResourceCount(ResourceType.ORE));

        // Negative resulting deduction (boundary, shouldn't go below 0)
        player.deductResource(ResourceType.ORE, 10);
        assertEquals(0, player.getResourceCount(ResourceType.ORE));

        // Normal deduction
        player.addResource(ResourceType.ORE, 5);
        player.deductResource(ResourceType.ORE, 2);
        assertEquals(3, player.getResourceCount(ResourceType.ORE));
    }

    /**
     * Partition testing for hasResources.
     * Tests different logical partitions:
     * - Partition 1: Exact resources needed
     * - Partition 2: More resources than needed (Surplus)
     * - Partition 3: Missing some resources
     * - Partition 4: Empty cost map (should always be true)
     */
    @Test
    void testHasResourcesPartition() {
        player.addResource(ResourceType.GRAIN, 2);
        player.addResource(ResourceType.WOOL, 2);

        // Partition 1: Exact resources
        Map<ResourceType, Integer> exactCost = new HashMap<>();
        exactCost.put(ResourceType.GRAIN, 2);
        exactCost.put(ResourceType.WOOL, 2);
        assertTrue(player.hasResources(exactCost), "Partition 1: Exact resources should return true");

        // Partition 2: Surplus resources
        Map<ResourceType, Integer> lesserCost = new HashMap<>();
        lesserCost.put(ResourceType.GRAIN, 1);
        lesserCost.put(ResourceType.WOOL, 1);
        assertTrue(player.hasResources(lesserCost), "Partition 2: Surplus resources should return true");

        // Partition 3: Missing resources
        Map<ResourceType, Integer> expensiveCost = new HashMap<>();
        expensiveCost.put(ResourceType.GRAIN, 3);
        expensiveCost.put(ResourceType.WOOL, 1);
        assertFalse(player.hasResources(expensiveCost), "Partition 3: Missing resources should return false");

        // Partition 4: Empty cost map
        Map<ResourceType, Integer> emptyCost = new HashMap<>();
        assertTrue(player.hasResources(emptyCost), "Partition 4: Empty cost should return true");
    }

    /**
     * Repeated addResource calls must accumulate, not overwrite.
     */
    @Test
    void testAddResourceAccumulatesAcrossMultipleCalls() {
        player.addResource(ResourceType.LUMBER, 3);
        player.addResource(ResourceType.LUMBER, 2);
        assertEquals(5, player.getResourceCount(ResourceType.LUMBER),
                "Multiple addResource calls must sum, not replace");
    }

    /**
     * Deducting from a zero balance must remain at 0 (Math.max guard).
     */
    @Test
    void testDeductFromZeroRemainsZero() {
        player.deductResource(ResourceType.WOOL, 5);
        assertEquals(0, player.getResourceCount(ResourceType.WOOL),
                "Deducting from 0 should leave 0, never go negative");
    }

    /**
     * getTotalResourceCards must sum across ALL 5 resource types correctly.
     */
    @Test
    void testTotalResourceCardsSpansAllTypes() {
        player.addResource(ResourceType.LUMBER, 1);
        player.addResource(ResourceType.BRICK,  2);
        player.addResource(ResourceType.GRAIN,  3);
        player.addResource(ResourceType.WOOL,   4);
        player.addResource(ResourceType.ORE,    5);
        assertEquals(15, player.getTotalResourceCards(),
                "Total must be the sum across all 5 resource types");
    }

    /**
     * hasResources must return false when a required resource type is completely absent
     * (i.e. the player has 0 of it, not even a reduced amount).
     */
    @Test
    void testHasResourcesMissingTypeReturnsFalse() {
        player.addResource(ResourceType.LUMBER, 5);
        // No BRICK added at all
        Map<ResourceType, Integer> cost = new HashMap<>();
        cost.put(ResourceType.LUMBER, 1);
        cost.put(ResourceType.BRICK,  1);
        assertFalse(player.hasResources(cost), "Missing resource type must return false");
    }
}
