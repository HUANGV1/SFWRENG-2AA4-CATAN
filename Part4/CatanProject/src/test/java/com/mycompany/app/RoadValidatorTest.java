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
}
