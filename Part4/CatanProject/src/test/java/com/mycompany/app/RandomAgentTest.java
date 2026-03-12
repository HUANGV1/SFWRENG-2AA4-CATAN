package com.mycompany.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RandomAgent.takeTurn() and handleOverSevenCards().
 *
 * Each test gives the agent ONLY the resources needed for
 * one specific action type, making the outcome deterministic despite the
 * agent's internal randomness.
 */
class RandomAgentTest {

    private Board board;
    private CatanEngine engine;
    private RandomAgent player;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        IBoardGraph topology = new CatanBoardGraph();
        board = new Board(topology);
        engine = new CatanEngine(board, new StandardDice());
        player = new RandomAgent(0);
        players = Arrays.asList(player, new RandomAgent(1));
        engine.setPlayers(players);
    }

    /**
     * Without any resources the agent can afford nothing. takeTurn() must return
     * without placing any road, settlement, or city.
     */
    @Test
    void testTakeTurnDoesNothingWithNoResources() {
        board.getNode(0).buildSettlement(player); // give anchor for road validity

        player.takeTurn(engine);

        long occupiedEdges = board.getAllEdges().stream()
                .filter(e -> e.getOccupant() != null).count();
        assertEquals(0, occupiedEdges, "No resources means no road should be built");
    }

    /**
     * With exactly 1 LUMBER + 1 BRICK (road cost) and a settlement at node 0,
     * the agent has exactly one affordable action: build a road.
     * No GRAIN or WOOL means settlement impossible. No ORE GRAIN means city impossible.
     * After takeTurn, LUMBER and BRICK should be consumed and one road placed.
     */
    @Test
    void testTakeTurnBuildsRoadWhenOnlyRoadResourcesAvailable() {
        board.getNode(0).buildSettlement(player);
        player.addResource(ResourceType.LUMBER, 1);
        player.addResource(ResourceType.BRICK,  1);

        player.takeTurn(engine);

        assertEquals(0, player.getResourceCount(ResourceType.LUMBER), "LUMBER must be consumed");
        assertEquals(0, player.getResourceCount(ResourceType.BRICK),  "BRICK must be consumed");
        long occupiedEdges = board.getAllEdges().stream()
                .filter(e -> e.getOccupant() != null).count();
        assertEquals(1, occupiedEdges, "Exactly one road must be built");
    }
}
