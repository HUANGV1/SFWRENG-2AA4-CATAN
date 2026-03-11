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

    /**
     * With 3 ORE + 2 GRAIN (city cost) and an existing settlement at node 0,
     * the only affordable action is upgrading to city.
     * No LUMBER/BRICK means road impossible. No WOOL means settlement impossible.
     */
    @Test
    void testTakeTurnUpgradesToCityWhenOnlyCityResourcesAndSettlement() {
        board.getNode(0).setOccupant(player, BuildingType.SETTLEMENT);
        player.addResource(ResourceType.ORE,   3);
        player.addResource(ResourceType.GRAIN, 2);

        player.takeTurn(engine);

        assertEquals(BuildingType.CITY, board.getNode(0).getType(),
                "Settlement must be upgraded to city");
        assertEquals(0, player.getResourceCount(ResourceType.ORE),
                "ORE must be fully consumed");
        assertEquals(0, player.getResourceCount(ResourceType.GRAIN),
                "GRAIN must be fully consumed");
    }

    /**
     * With LUMBER + BRICK + GRAIN + WOOL (settlement cost) and no prior buildings,
     * the agent is in initial-placement mode (no road needed). Valid settlements
     * exist across all 54 nodes. The agent must place exactly one settlement.
     * No adjacency or city resources added means only settlement action is possible.
     */
    @Test
    void testTakeTurnBuildsSettlementInInitialPlacement() {
        player.addResource(ResourceType.LUMBER, 1);
        player.addResource(ResourceType.BRICK,  1);
        player.addResource(ResourceType.GRAIN,  1);
        player.addResource(ResourceType.WOOL,   1);

        player.takeTurn(engine);

        long occupiedNodes = board.getAllNodes().stream()
                .filter(n -> n.getOccupant() != null
                        && n.getOccupant().getPlayerID() == player.getPlayerID())
                .count();
        assertEquals(1, occupiedNodes, "One settlement must be placed");
        assertEquals(0, player.getResourceCount(ResourceType.LUMBER), "LUMBER consumed");
        assertEquals(0, player.getResourceCount(ResourceType.BRICK),  "BRICK consumed");
    }

    /**
     * handleOverSevenCards() must not throw even when the player has >7 cards
     * and no controller reference is available (the current implementation is
     * a no-op stub that breaks early when it cannot build).
     */
    @Test
    void testHandleOverSevenCardsDoesNotCrash() {
        player.addResource(ResourceType.LUMBER, 5);
        player.addResource(ResourceType.BRICK,  5);  // total = 10 > 7

        assertDoesNotThrow(() -> player.handleOverSevenCards(),
                "handleOverSevenCards must never throw");
    }

    /**
     * After handleOverSevenCards(), the player's card count must not increase
     * (the stub cannot build without a controller, so count stays the same).
     */
    @Test
    void testHandleOverSevenCardsDoesNotAwardCards() {
        player.addResource(ResourceType.LUMBER, 5);
        player.addResource(ResourceType.BRICK,  5);
        int before = player.getTotalResourceCards();

        player.handleOverSevenCards();

        assertEquals(before, player.getTotalResourceCards(),
                "Card count must not increase after handleOverSevenCards");
    }
}
