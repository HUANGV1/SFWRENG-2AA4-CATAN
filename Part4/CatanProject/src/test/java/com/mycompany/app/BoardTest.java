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
     * Tile 0 is hardcoded as WHEAT with token 6 in Board.initializeBoard().
     * Tests that getTile returns the right tile and its properties are correct.
     */
    @Test
    void testGetTileValidProperties() {
        HexTile tile = board.getTile(0);
        assertNotNull(tile);
        assertEquals(0, tile.getTileID());
        assertEquals(TileType.WHEAT, tile.getType());
        assertEquals(6, tile.getNumberToken());
    }

    /**
     * Out-of-bounds tile IDs should return null - callers depend on this to skip
     * invalid tiles during resource distribution.
     */
    @Test
    void testGetTileOutOfBoundsReturnsNull() {
        assertNull(board.getTile(-1));
        assertNull(board.getTile(19));
    }

    /**
     * Tile 11 is the DESERT tile. It must have token 0 and return null resource type.
     * This is what prevents the desert from producing resources.
     */
    @Test
    void testDesertTileHasNoTokenAndNoResource() {
        HexTile desert = board.getTile(11);
        assertNotNull(desert);
        assertEquals(TileType.DESERT, desert.getType());
        assertEquals(0, desert.getNumberToken());
        assertNull(desert.getType().getResourceType(), "Desert should produce no resource");
    }

    /**
     * Partition test: Test valid vs invalid node retrieval,
     */
    @Test
    void testGetNodeValidAndInvalid() {
        // Valid partition (corners of hexes 0-53)
        Node validNode = board.getNode(0);
        assertNotNull(validNode);
        assertEquals(0, validNode.getNodeID());

        // Invalid partition (negative or out of bounds)
        assertNull(board.getNode(-1));
        assertNull(board.getNode(100)); // Out of bounds for standard map
    }

    /**
     * State mutability test for R2.2 Visualizer and R2.3 JSON.
     * Ensure occupants can be successfully set/read on nodes.
     */
    @Test
    void testNodeOccupancyState() {
        Node node = board.getNode(10);
        assertNull(node.getOccupant());
        assertEquals(BuildingType.NONE, node.getType());

        Player player = new RandomAgent(2);

        node.setOccupant(player, BuildingType.SETTLEMENT);

        assertNotNull(node.getOccupant());
        assertEquals(2, node.getOccupant().getPlayerID());
        assertEquals(BuildingType.SETTLEMENT, node.getType());
    }

    /**
     * Test retrieving edges to ensure graph linkages remain intact.
     */
    @Test
    void testGetEdgeValidAndInvalid() {
        // Valid
        Edge validEdge = board.getEdge(0);
        assertNotNull(validEdge);
        assertEquals(0, validEdge.getEdgeID());

        // Invalid
        assertNull(board.getEdge(-1));
        assertNull(board.getEdge(150));
    }
}
