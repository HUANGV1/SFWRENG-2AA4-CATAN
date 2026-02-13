package catan;

import catan.board.Board;
import catan.board.CatanBoardGraph;
import catan.engine.*;
import catan.enums.BuildingCost;
import catan.enums.BuildingType;
import catan.enums.ResourceType;
import catan.interfaces.IBoardGraph;
import catan.model.Edge;
import catan.model.Node;
import catan.player.Player;
import catan.player.RandomAgent;
import catan.simulator.Simulator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatanEngineTest {

    private IBoardGraph graph;
    private Board board;
    private CatanEngine engine;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        graph = new CatanBoardGraph();
        board = new Board(graph, 12345L);
        StandardDice dice = new StandardDice(12345L);
        engine = new CatanEngine(board, dice);
        player1 = new RandomAgent(1, 100L);
        player2 = new RandomAgent(2, 200L);
        engine.setPlayers(List.of(player1, player2));
    }

    // ---- Topology ----

    @Test
    @DisplayName("Board should have exactly 54 nodes, 72 edges, 19 tiles")
    void testBoardStructure() {
        CatanBoardGraph testGraph = new CatanBoardGraph();
        assertEquals(54, testGraph.getTotalNodes(), "Should have 54 nodes");
        assertEquals(72, testGraph.getTotalEdges(), "Should have 72 edges");
        assertEquals(19, testGraph.getTotalTiles(), "Should have 19 tiles");
    }

    // ---- Distance Rule ----

    @Test
    @DisplayName("Settlement placement should succeed on empty node during setup")
    void testSettlementPlacementSetup() {
        boolean result = engine.requestBuildSettlement(1, 13, true);
        assertTrue(result, "Should place settlement during setup");
        assertEquals(1, player1.getVictoryPoints(), "Player should have 1 VP");
    }

    @Test
    @DisplayName("Settlement placement should fail on occupied node")
    void testSettlementOnOccupiedNode() {
        engine.requestBuildSettlement(1, 13, true);
        boolean result = engine.requestBuildSettlement(2, 13, true);
        assertFalse(result, "Should not place on occupied node");
    }

    @Test
    @DisplayName("Distance rule should prevent adjacent settlements")
    void testDistanceRule() {
        engine.requestBuildSettlement(1, 13, true);
        List<Integer> adjacent = graph.getAdjacentNodes(13);
        assertFalse(adjacent.isEmpty(), "Node 13 should have adjacent nodes");

        for (int adjNode : adjacent) {
            boolean result = engine.requestBuildSettlement(2, adjNode, true);
            assertFalse(result, "Distance rule violated at node " + adjNode);
        }
    }

    // ---- Road Connectivity ----

    @Test
    @DisplayName("Road placement should succeed adjacent to settlement during setup")
    void testRoadPlacementSetup() {
        engine.requestBuildSettlement(1, 13, true);
        List<Integer> adjEdges = graph.getAdjacentEdges(13);
        assertFalse(adjEdges.isEmpty(), "Should have adjacent edges");

        boolean result = engine.requestBuildRoad(1, adjEdges.get(0), true);
        assertTrue(result, "Should place road during setup");
    }

    @Test
    @DisplayName("Road placement should fail when disconnected from network")
    void testRoadConnectivity() {
        engine.requestBuildSettlement(1, 0, true);
        List<Integer> adjEdges = graph.getAdjacentEdges(0);
        if (!adjEdges.isEmpty()) {
            engine.requestBuildRoad(1, adjEdges.get(0), true);
        }

        player1.addResource(ResourceType.LUMBER, 1);
        player1.addResource(ResourceType.BRICK, 1);

        // Find a disconnected edge
        int disconnectedEdge = -1;
        for (int edgeId = 0; edgeId < 72; edgeId++) {
            if (!board.getEdge(edgeId).hasRoad()) {
                List<Integer> endpoints = graph.getEdgeEndpoints(edgeId);
                boolean connected = false;
                for (int nodeId : endpoints) {
                    if (board.getNode(nodeId).isOccupiedBy(1)) {
                        connected = true;
                        break;
                    }
                    for (int adjEdge : graph.getAdjacentEdges(nodeId)) {
                        if (board.getEdge(adjEdge).getOccupant() != null
                                && board.getEdge(adjEdge).getOccupant().getPlayerID() == 1) {
                            connected = true;
                            break;
                        }
                    }
                    if (connected)
                        break;
                }
                if (!connected) {
                    disconnectedEdge = edgeId;
                    break;
                }
            }
        }

        if (disconnectedEdge >= 0) {
            boolean result = engine.requestBuildRoad(1, disconnectedEdge);
            assertFalse(result, "Should not build disconnected road");
        }
    }

    // ---- City Upgrade ----

    @Test
    @DisplayName("City upgrade should work on player's own settlement")
    void testCityUpgrade() {
        engine.requestBuildSettlement(1, 13, true);
        assertEquals(1, player1.getVictoryPoints());

        player1.addResource(ResourceType.GRAIN, 2);
        player1.addResource(ResourceType.ORE, 3);

        boolean result = engine.requestBuildCity(1, 13);
        assertTrue(result, "Should upgrade settlement to city");
        assertEquals(2, player1.getVictoryPoints(), "City should give 2 VP total");
        assertEquals(BuildingType.CITY, board.getNode(13).getType());
    }

    @Test
    @DisplayName("City upgrade should fail without enough resources")
    void testCityUpgradeInsufficientResources() {
        engine.requestBuildSettlement(1, 13, true);
        player1.addResource(ResourceType.GRAIN, 1);
        player1.addResource(ResourceType.ORE, 3);

        boolean result = engine.requestBuildCity(1, 13);
        assertFalse(result, "Should not upgrade without enough grain");
    }

    // ---- Resource Costs ----

    @Test
    @DisplayName("Settlement requires 1 lumber, 1 brick, 1 grain, 1 wool")
    void testSettlementResourceCost() {
        assertFalse(player1.hasResources(BuildingCost.SETTLEMENT.getCost()));

        player1.addResource(ResourceType.LUMBER, 1);
        player1.addResource(ResourceType.BRICK, 1);
        assertFalse(player1.hasResources(BuildingCost.SETTLEMENT.getCost()));

        player1.addResource(ResourceType.GRAIN, 1);
        player1.addResource(ResourceType.WOOL, 1);
        assertTrue(player1.hasResources(BuildingCost.SETTLEMENT.getCost()));
    }

    @Test
    @DisplayName("Road requires 1 lumber, 1 brick")
    void testRoadResourceCost() {
        assertFalse(player1.hasResources(BuildingCost.ROAD.getCost()));

        player1.addResource(ResourceType.LUMBER, 1);
        assertFalse(player1.hasResources(BuildingCost.ROAD.getCost()));

        player1.addResource(ResourceType.BRICK, 1);
        assertTrue(player1.hasResources(BuildingCost.ROAD.getCost()));
    }

    // ---- Dice ----

    @Test
    @DisplayName("Standard dice should produce values between 2 and 12")
    void testDiceRoll() {
        StandardDice dice = new StandardDice(999L);
        for (int i = 0; i < 100; i++) {
            int roll = dice.roll();
            assertTrue(roll >= 2 && roll <= 12, "Dice roll should be between 2 and 12");
        }
    }

    // ---- Simulator ----

    @Test
    @DisplayName("Simulator should complete a game within the round limit")
    void testSimulatorCompletion() {
        Simulator simulator = new Simulator(500, 42L);
        simulator.runSimulation();

        assertTrue(simulator.getCurRounds() > 0, "Game should have at least one round");
        assertTrue(simulator.getCurRounds() <= 500, "Game should not exceed max rounds");
    }

    @Test
    @DisplayName("Simulator should reject invalid round limits")
    void testSimulatorInvalidRoundLimit() {
        assertThrows(IllegalArgumentException.class, () -> new Simulator(0));
    }

    // ---- Robber ----

    @Test
    @DisplayName("Players should discard half when having more than 7 cards on robber")
    void testRobberDiscard() {
        RandomAgent agent = new RandomAgent(1, 555L);
        agent.addResource(ResourceType.LUMBER, 4);
        agent.addResource(ResourceType.BRICK, 3);
        agent.addResource(ResourceType.GRAIN, 3);

        assertEquals(10, agent.getTotalResourceCount());
        agent.handleOverSevenCards();
        assertEquals(5, agent.getTotalResourceCount(), "Should discard half (5 cards)");
    }

    @Test
    @DisplayName("Player with 7 or fewer cards should not discard on robber")
    void testRobberNoDiscard() {
        RandomAgent agent = new RandomAgent(1, 555L);
        agent.addResource(ResourceType.LUMBER, 3);
        agent.addResource(ResourceType.BRICK, 4);

        assertEquals(7, agent.getTotalResourceCount());
        agent.handleOverSevenCards();
        assertEquals(7, agent.getTotalResourceCount(), "Should not discard with 7 cards");
    }

    // ---- Validators (SRP) ----

    @Test
    @DisplayName("SettlementValidator should enforce distance rule")
    void testSettlementValidatorDistanceRule() {
        SettlementValidator validator = new SettlementValidator(board);

        assertTrue(validator.isValid(1, 13, true), "Should be valid initially");

        // Place a settlement
        board.getNode(13).buildSettlement(player1);

        // Adjacent nodes should be invalid
        for (int adj : graph.getAdjacentNodes(13)) {
            assertFalse(validator.isValid(2, adj, true),
                    "Adjacent node " + adj + " should be invalid");
        }
    }

    @Test
    @DisplayName("RoadValidator should reject disconnected roads")
    void testRoadValidatorDisconnected() {
        RoadValidator validator = new RoadValidator(board, graph);

        // No structures on board, all roads should be invalid
        assertFalse(validator.isValid(1, 0), "Should not build without network");
    }
}
