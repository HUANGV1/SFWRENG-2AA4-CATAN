package com.mycompany.app;

import com.mycompany.app.services.ResourceDistributor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ResourceDistributor.
 *
 * Board tile configuration used below:
 *   Tile 0  = WHEAT, token 6,  adjacent nodes {0,1,2,3,4,5}
 *   Tile 1  = WOOD,  token 3,  adjacent nodes {0,1,6,7}
 *   Tile 10 = BRICK, token 6,  adjacent nodes {27,28,29}
 *   Tile 11 = DESERT, token 0, adjacent nodes {30,31,32}
 */
class ResourceDistributorTest {

    private Board board;
    private ResourceDistributor distributor;
    private Player player0;
    private Player player1;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        IBoardGraph topology = new CatanBoardGraph();
        board = new Board(topology);
        distributor = new ResourceDistributor(board, topology);
        player0 = new RandomAgent(0);
        player1 = new RandomAgent(1);
        players = Arrays.asList(player0, player1);
    }

    /**
     * A settlement on a WHEAT tile (token 6) must receive exactly 1 GRAIN
     * when 6 is rolled. Tile 0 = WHEAT/6, node 0 is adjacent.
     */
    @Test
    void testSettlementReceivesOneResourceOnMatchingRoll() {
        board.getNode(0).buildSettlement(player0);

        distributor.distribute(6, players);

        assertEquals(1, player0.getResourceCount(ResourceType.GRAIN),
                "Settlement on WHEAT/6 must yield 1 GRAIN on roll 6");
    }

    /**
     * A city on a matching tile must receive 2 resources, not 1.
     * The ResourceDistributor multiplies by 2 for CITY nodes.
     */
    @Test
    void testCityReceivesTwoResourcesOnMatchingRoll() {
        board.getNode(0).setOccupant(player0, BuildingType.CITY);

        distributor.distribute(6, players);

        assertEquals(2, player0.getResourceCount(ResourceType.GRAIN),
                "City on WHEAT/6 must yield 2 GRAIN on roll 6");
    }

    /**
     * Roll of 7 (robber) is explicitly guarded in ResourceDistributor.distribute()
     * and must never award any resources.
     */
    @Test
    void testNoResourceOnRollSeven() {
        board.getNode(0).buildSettlement(player0);

        distributor.distribute(7, players);

        assertEquals(0, player0.getTotalResourceCards(),
                "Roll of 7 must not distribute any resources");
    }

    /**
     * Both players receive resources when their settlements are on different
     * nodes of the same tile. Tile 0 = WHEAT/6; player0 at node 0, player1 at node 1.
     */
    @Test
    void testMultiplePlayersReceiveResourcesFromSameTile() {
        board.getNode(0).buildSettlement(player0);
        board.getNode(1).buildSettlement(player1);

        distributor.distribute(6, players);

        assertEquals(1, player0.getResourceCount(ResourceType.GRAIN),
                "Player 0 (node 0, WHEAT/6) must receive 1 GRAIN");
        assertEquals(1, player1.getResourceCount(ResourceType.GRAIN),
                "Player 1 (node 1, WHEAT/6) must receive 1 GRAIN");
    }

    /**
     * With no settlements on the board, no player should receive any resources.
     */
    @Test
    void testNoResourceWhenBoardIsEmpty() {
        distributor.distribute(6, players);

        assertEquals(0, player0.getTotalResourceCards());
        assertEquals(0, player1.getTotalResourceCards());
    }

    /**
     * Rolling a number that matches no tile token must distribute nothing.
     * Token 13 never appears on any standard Catan tile.
     */
    @Test
    void testNoResourceForUnmatchedRollToken() {
        board.getNode(0).buildSettlement(player0);

        distributor.distribute(13, players);

        assertEquals(0, player0.getTotalResourceCards(),
                "Token 13 does not exist on any tile - no resources distributed");
    }

    /**
     * Rolling a number that matches a tile but no occupant on that tile's nodes
     * must distribute nothing.
     */
    @Test
    void testNoResourceWhenMatchingTileHasNoOccupant() {
        // Tile 0 has token 6 but we place no settlement on its nodes (0-5)
        distributor.distribute(6, players);

        assertEquals(0, player0.getTotalResourceCards());
        assertEquals(0, player1.getTotalResourceCards());
    }

    /**
     * A roll matching a second tile (tile 10 = BRICK/6, nodes 27-29) must give
     * the correct resource type (BRICK), independently of tile 0 (WHEAT/6).
     */
    @Test
    void testCorrectResourceTypeFromDifferentTile() {
        // Node 27 is on tile 10 (BRICK, token 6)
        board.getNode(27).buildSettlement(player0);

        distributor.distribute(6, players);

        assertEquals(1, player0.getResourceCount(ResourceType.BRICK),
                "Node 27 on BRICK/6 tile must yield 1 BRICK on roll 6");
        assertEquals(0, player0.getResourceCount(ResourceType.GRAIN),
                "GRAIN must not be awarded from the BRICK tile");
    }

    /**
     * When both tiles with token 6 have occupants (tile 0 = WHEAT node 0,
     * tile 10 = BRICK node 27), rolling 6 must award both resource types.
     */
    @Test
    void testBothMatchingTilesDistributeOnSameRoll() {
        board.getNode(0).buildSettlement(player0);   // WHEAT/6
        board.getNode(27).buildSettlement(player0);  // BRICK/6

        distributor.distribute(6, players);

        assertEquals(1, player0.getResourceCount(ResourceType.GRAIN),
                "Node 0 (WHEAT/6) must yield 1 GRAIN");
        assertEquals(1, player0.getResourceCount(ResourceType.BRICK),
                "Node 27 (BRICK/6) must yield 1 BRICK");
    }
}
