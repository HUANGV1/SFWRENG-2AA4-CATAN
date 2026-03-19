package com.mycompany.app;

import com.mycompany.app.validators.RoadValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RoadValidator.
 *
 * Uses CatanBoardGraph topology (matches visualizer).
 * Node 0 adjacent edges: {4, 5, 23}
 * Edge 4: {5, 0}, Edge 5: {0, 1}, Edge 23: {20, 0}
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
     * Node 0 adjacent edges in CatanBoardGraph: 4, 5, 23.
     */
    @Test
    void testRoadValidAdjacentToSettlement() {
        board.getNode(0).buildSettlement(player);
        assertTrue(validator.isValid(0, 4), "Edge 4 connects to node 0 (player's settlement) - must be valid");
        assertTrue(validator.isValid(0, 5), "Edge 5 connects to node 0 (player's settlement) - must be valid");
        assertTrue(validator.isValid(0, 23), "Edge 23 connects to node 0 (player's settlement) - must be valid");
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
