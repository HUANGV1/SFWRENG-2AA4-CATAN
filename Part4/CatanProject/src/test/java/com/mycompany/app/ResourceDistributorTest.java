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
     * Roll of 7 (robber) must never award any resources (Person 2).
     */
    @Test
    void testNoResourceOnRollSeven() {
        board.getNode(0).buildSettlement(player0);
        distributor.distribute(7, players);
        assertEquals(0, player0.getTotalResourceCards(),
                "Roll of 7 must not distribute any resources");
    }
}
