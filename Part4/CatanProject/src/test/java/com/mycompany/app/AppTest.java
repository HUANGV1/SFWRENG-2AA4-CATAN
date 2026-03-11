package com.mycompany.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for foundational enum types that the entire game depends on.
 */
public class AppTest {

    /**
     * Verifies all 5 resource types exist - any missing type breaks resource
     * tracking throughout the game.
     */
    @Test
    public void testResourceTypeHasFiveValues() {
        ResourceType[] types = ResourceType.values();
        assertEquals(5, types.length, "Must have exactly 5 resource types: LUMBER, BRICK, GRAIN, WOOL, ORE");
    }

    /**
     * Verifies TileType.getResourceType() maps each tile to the correct resource.
     * This mapping is used by ResourceDistributor to award resources on dice rolls.
     */
    @Test
    public void testTileTypeResourceMapping() {
        assertEquals(ResourceType.LUMBER, TileType.WOOD.getResourceType());
        assertEquals(ResourceType.BRICK,  TileType.BRICK.getResourceType());
        assertEquals(ResourceType.GRAIN,  TileType.WHEAT.getResourceType());
        assertEquals(ResourceType.WOOL,   TileType.SHEEP.getResourceType());
        assertEquals(ResourceType.ORE,    TileType.ORE.getResourceType());
        assertNull(TileType.DESERT.getResourceType(), "Desert produces no resource");
    }

    /**
     * Verifies all 6 tile types exist in the enum.
     */
    @Test
    public void testTileTypeHasSixValues() {
        assertEquals(6, TileType.values().length,
                "Must have exactly 6 tile types: WOOD, BRICK, WHEAT, SHEEP, ORE, DESERT");
    }
}
