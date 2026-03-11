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

    /**
     * The IRandomDice lambda injection must be called by rollDice().
     */
    @Test
    void testRollDiceDeterminism() {
        assertEquals(7, engine.rollDice(), "Mock dice should reliably return 7");
    }

    /**
     * On an empty board every node passes initial-placement validation
     * (no distance-2 neighbours occupied, no road required).
     */
    @Test
    void testValidSettlementLocationsOnEmptyBoard() {
        int[] validNodes = engine.getValidSettlementLocations(0);
        assertNotNull(validNodes);
        assertEquals(54, validNodes.length, "All 54 nodes are valid for initial placement on an empty board");
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
     * City upgrade must fail when the player lacks sufficient resources.
     * Node must remain a settlement; resources must be unmodified.
     */
    @Test
    void testRequestBuildCityFailsWithInsufficientResources() {
        Player player = players.get(0);
        Node targetNode = board.getNode(2);
        targetNode.setOccupant(player, BuildingType.SETTLEMENT);

        player.addResource(ResourceType.ORE, 2);  // needs 3
        player.addResource(ResourceType.GRAIN, 2);

        assertFalse(engine.requestBuildCity(0, 2), "Must fail with only 2 ORE");
        assertEquals(BuildingType.SETTLEMENT, targetNode.getType(), "Node must stay as SETTLEMENT");
        assertEquals(2, player.getResourceCount(ResourceType.ORE), "ORE must not be deducted on failure");
    }

    /**
     * City upgrade must fail when the target node has no settlement at all.
     */
    @Test
    void testRequestBuildCityRequiresExistingSettlement() {
        Player player = players.get(0);
        player.addResource(ResourceType.ORE, 3);
        player.addResource(ResourceType.GRAIN, 2);

        assertFalse(engine.requestBuildCity(0, 3), "Cannot build city on an empty node");
    }

    /**
     * Distance-2 rule: placing a settlement at node 2 must block nodes 1 and 3
     * (its only adjacents per the topology) from appearing in valid locations.
     */
    @Test
    void testDistance2RuleBlocksAdjacentNodes() {
        Player player = players.get(0);
        board.getNode(2).buildSettlement(player);

        // Ask for player 1's valid placements - distance-2 applies regardless of owner
        int[] valid = engine.getValidSettlementLocations(1);
        for (int nodeID : valid) {
            assertNotEquals(1, nodeID, "Node 1 is adjacent to occupied node 2 - must be blocked");
            assertNotEquals(3, nodeID, "Node 3 is adjacent to occupied node 2 - must be blocked");
        }
    }

    /**
     * Once a player has ≥1 building, getValidSettlementLocations switches to
     * requiring an adjacent road. A player with a settlement but no roads
     * must see zero valid locations for a second settlement.
     */
    @Test
    void testNonInitialSettlementRequiresAdjacentRoad() {
        Player player = players.get(0);
        board.getNode(0).buildSettlement(player);

        int[] valid = engine.getValidSettlementLocations(0);
        assertEquals(0, valid.length, "Player with 1 building and no roads has no valid settlement locations");
    }

    /**
     * distributeResources(6, ...) must give GRAIN to a player whose settlement
     * sits on tile 0 (WHEAT, token 6, adjacent nodes 0-5).
     */
    @Test
    void testResourceDistributionSettlementGetsOne() {
        CatanEngine engineWith6 = new CatanEngine(board, () -> 6);
        engineWith6.setPlayers(players);
        Player player = players.get(0);
        board.getNode(0).buildSettlement(player);

        engineWith6.distributeResources(6, players);

        assertEquals(1, player.getResourceCount(ResourceType.GRAIN),
                "Settlement on WHEAT/6 tile should receive exactly 1 GRAIN on roll 6");
    }

    /**
     * A city on tile 0 (WHEAT, token 6) must receive 2 GRAIN, not 1.
     */
    @Test
    void testResourceDistributionCityGetsTwo() {
        CatanEngine engineWith6 = new CatanEngine(board, () -> 6);
        engineWith6.setPlayers(players);
        Player player = players.get(0);
        board.getNode(0).setOccupant(player, BuildingType.CITY);

        engineWith6.distributeResources(6, players);

        assertEquals(2, player.getResourceCount(ResourceType.GRAIN),
                "City on WHEAT/6 tile should receive 2 GRAIN on roll 6");
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

    /**
     * A roll that matches no tile token distributes nothing.
     */
    @Test
    void testNoDistributionForUnmatchedRoll() {
        Player player = players.get(0);
        board.getNode(0).buildSettlement(player);

        engine.distributeResources(13, players); // no tile has token 13

        assertEquals(0, player.getTotalResourceCards());
    }
}
