package com.mycompany.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatanEngineTest {
    private CatanEngine engine;
    private Board board;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        IBoardGraph topology = new CatanBoardGraph();
        board = new Board(topology);
        engine = new CatanEngine(board, () -> 7);
        players = new ArrayList<>();
        players.add(new RandomAgent(0));
        players.add(new RandomAgent(1));
        engine.setPlayers(players);
    }

    
    @Test
    void testRollDiceDeterminism() {
        assertEquals(7, engine.rollDice(), "Mock dice should reliably return 7");
    }

    /**
     * requestBuildCity must deduct BOTH ORE and GRAIN (city costs 3 ORE + 2 GRAIN).
     * Previous version of this test only asserted ORE, missing the GRAIN deduction.
     */
    @Test
    void testRequestBuildCityDeductsBothOreAndGrain() {
        Player player = players.get(0);
        Node targetNode = board.getNode(1);
        targetNode.setOccupant(player, BuildingType.SETTLEMENT);

        player.addResource(ResourceType.ORE, 3);
        player.addResource(ResourceType.GRAIN, 2);

        boolean success = engine.requestBuildCity(0, 1);

        assertTrue(success, "Should succeed with exactly 3 ORE + 2 GRAIN");
        assertEquals(BuildingType.CITY, targetNode.getType(), "Node must be upgraded to CITY");
        assertEquals(0, player.getResourceCount(ResourceType.ORE),   "3 ORE must be fully consumed");
        assertEquals(0, player.getResourceCount(ResourceType.GRAIN), "2 GRAIN must be fully consumed");
    }

    /**
     * Roll of 7 (robber) must never distribute any resources.
     */
    @Test
    void testNoResourceDistributionOnSeven() {
        Player player = players.get(0);
        board.getNode(0).buildSettlement(player);

        engine.distributeResources(7, players);

        assertEquals(0, player.getTotalResourceCards(),
                "Roll of 7 must not distribute any resources");
    }
}
