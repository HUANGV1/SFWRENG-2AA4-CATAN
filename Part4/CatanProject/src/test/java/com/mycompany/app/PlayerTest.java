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
}
