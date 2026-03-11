package com.mycompany.app;

import com.mycompany.app.validators.RoadValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RoadValidator.
 *
 * Board topology used:
 *   Center hex edges (0-5): edge 0={0,1}, edge 1={1,2}, ..., edge 5={5,0}
 *   Center-to-inner edges: edge 6={0,6}, edge 7={0,7}, edge 8={1,8}, edge 9={1,9}, ...
 *   Inner ring edges: edge 18={6,7}, edge 21={9,10}, edge 22={10,11}, ...
 *   Outer ring edges: edge 30={18,19}, ...
 *
 * Node 0 adjacent edges: {0, 5, 6, 7}
 */
class RoadValidatorTest {

    private Board board;
    private RoadValidator validator;
    private Player player;

    @BeforeEach
    void setUp() {
        IBoardGraph topology = new CatanBoardGraph();
        board = new Board(topology);
        validator = new RoadValidator(board, topology);
        player = new RandomAgent(0);
    }

    /**
     * A road adjacent to a player's own settlement must be valid.
     * Edge 0 connects nodes {0,1}; player has settlement at node 0.
     */
    @Test
    void testRoadValidAdjacentToSettlement() {
        board.getNode(0).buildSettlement(player);
        assertTrue(validator.isValid(0, 0), "Edge 0 connects to node 0 (player's settlement) - must be valid");
        assertTrue(validator.isValid(0, 5), "Edge 5 connects to node 0 (player's settlement) - must be valid");
        assertTrue(validator.isValid(0, 6), "Edge 6 connects to node 0 (player's settlement) - must be valid");
    }

    /**
     * An edge with no player structure or road nearby must be invalid.
     * Edge 30 connects outer ring nodes {18,19} - no player structures exist there.
     */
    @Test
    void testRoadInvalidWithNoAdjacentStructure() {
        assertFalse(validator.isValid(0, 30),
                "Edge 30 has no adjacent player structure - must be invalid");
    }

    /**
     * A road cannot be placed on an already-occupied edge.
     */
    @Test
    void testRoadInvalidOnAlreadyOccupiedEdge() {
        board.getNode(0).buildSettlement(player);
        board.getEdge(0).buildRoad(player); // manually occupy edge 0
        assertFalse(validator.isValid(0, 0),
                "Edge 0 already has a road - must be rejected");
    }

    /**
     * A road can extend along an existing road chain.
     * Player has settlement at node 0, road on edge 0 (nodes 0-1).
     * Edge 1 connects nodes {1,2}; node 1 is an endpoint of the existing road,
     * so edge 1 is reachable.
     */
    @Test
    void testRoadValidExtendingExistingRoad() {
        board.getNode(0).buildSettlement(player);
        board.getEdge(0).buildRoad(player);
        assertTrue(validator.isValid(0, 1),
                "Edge 1 is reachable via existing road on edge 0 through node 1");
    }

    /**
     * Negative or out-of-bounds edge IDs must return false without throwing.
     */
    @Test
    void testRoadInvalidOutOfBoundsEdgeIds() {
        assertFalse(validator.isValid(0, -1),  "Negative edge ID must return false");
        assertFalse(validator.isValid(0, 200), "Out-of-bounds edge ID must return false");
    }

    /**
     * With no player structures on the board, getValidLocations must return
     * an empty array (there is nowhere to anchor a road).
     */
    @Test
    void testGetValidRoadLocationsEmptyBoard() {
        int[] valid = validator.getValidLocations(0);
        assertEquals(0, valid.length,
                "No structures on board means no valid road placements");
    }

    /**
     * With a settlement at node 0, the returned valid locations must include
     * at least the four directly adjacent edges {0, 5, 6, 7}.
     */
    @Test
    void testGetValidRoadLocationsWithSettlement() {
        board.getNode(0).buildSettlement(player);
        int[] valid = validator.getValidLocations(0);
        assertTrue(valid.length >= 4,
                "Settlement at node 0 should unlock at least 4 adjacent road slots");

        boolean containsEdge0 = false;
        for (int e : valid) {
            if (e == 0) { containsEdge0 = true; break; }
        }
        assertTrue(containsEdge0, "Edge 0 must appear in the valid road locations");
    }

    /**
     * Player 1 must not be able to place a road adjacent only to player 0's structure.
     * Roads require the owning player's structure or road, not another player's.
     */
    @Test
    void testRoadInvalidForDifferentPlayer() {
        board.getNode(0).buildSettlement(player); // player 0 has settlement
        assertFalse(validator.isValid(1, 0),
                "Player 1 has no structures - edge 0 must be invalid for player 1");
    }

    /**
     * Road validity must also apply to edges adjacent to a City, not only settlements.
     */
    @Test
    void testRoadValidAdjacentToCity() {
        board.getNode(0).setOccupant(player, BuildingType.CITY);
        assertTrue(validator.isValid(0, 0),
                "Edge adjacent to a city must be a valid road location");
    }
}
