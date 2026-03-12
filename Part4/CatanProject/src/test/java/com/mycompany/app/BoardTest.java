package com.mycompany.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    private IBoardGraph topology;

    @BeforeEach
    void setUp() {
        topology = new CatanBoardGraph();
        board = new Board(topology);
    }

    /**
     * Standard Catan board must have exactly 54 nodes, 72 edges, 19 tiles.
     * Incorrect counts would silently break all placement logic.
     */
    @Test
    void testBoardInitializationCounts() {
        assertEquals(54, board.getAllNodes().size(), "Standard board has 54 nodes");
        assertEquals(72, board.getAllEdges().size(), "Standard board has 72 edges");
        assertEquals(19, board.getAllTiles().size(), "Standard board has 19 tiles");
    }

    /**
     * Boundary: out-of-bounds tile IDs should return null.
     */
    @Test
    void testGetTileOutOfBoundsReturnsNull() {
        assertNull(board.getTile(-1));
        assertNull(board.getTile(19));
    }
}
