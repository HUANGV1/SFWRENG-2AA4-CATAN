package com.mycompany.app;

import com.mycompany.app.commands.BuildRoadCommand;
import com.mycompany.app.commands.BuildSettlementCommand;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AgentConstraintFacade R3.3 constraints: over-7 spending,
 * road bridging (distance-2 segments), and longest-road competition.
 */
class AgentConstraintFacadeTest {

    private Board board;
    private IBoardGraph topology;
    private CatanEngine engine;
    private RuleBasedAgent agent;
    private RuleBasedAgent rival;

    @BeforeEach
    void setUp() {
        topology = new CatanBoardGraph();
        board = new Board(topology);
        engine = new CatanEngine(board, () -> 4);
        agent = new RuleBasedAgent(0, engine);
        rival = new RuleBasedAgent(1, engine);
        engine.setPlayers(List.of(agent, rival));
    }

    @Test
    void overSevenCardsTriggersBuildRoadCommand() {
        agent.addResource(ResourceType.LUMBER, 2);
        agent.addResource(ResourceType.BRICK, 2);
        agent.addResource(ResourceType.GRAIN, 2);
        agent.addResource(ResourceType.WOOL, 2);
        agent.addResource(ResourceType.ORE, 1);
        assertTrue(agent.getTotalResourceCards() > 7, "Agent must have >7 cards");

        board.getNode(1).buildSettlement(agent);
        int[] validRoads = engine.getValidRoadLocations(agent.getPlayerID());
        Assumptions.assumeTrue(validRoads != null && validRoads.length > 0,
                "Agent needs at least one valid road location");

        AgentConstraintFacade facade = new AgentConstraintFacade(engine);
        ICommand result = facade.getPriorityConstraintAction(agent);

        assertNotNull(result);
        assertInstanceOf(BuildRoadCommand.class, result);
        int edgeId = ((BuildRoadCommand) result).getEdgeId();
        assertTrue(Arrays.stream(engine.getValidRoadLocations(agent.getPlayerID())).anyMatch(e -> e == edgeId),
                "Returned edge must be in valid road locations");
    }

    @Test
    void twoRoadSegmentsWithinTwoReturnsBridgingBuildRoadCommand() {
        agent.addResource(ResourceType.LUMBER, 1);
        agent.addResource(ResourceType.BRICK, 1);

        board.getNode(1).buildSettlement(agent);
        board.getNode(4).buildSettlement(agent);
        board.getEdge(0).buildRoad(agent);
        board.getEdge(2).buildRoad(agent);

        int[] validRoads = engine.getValidRoadLocations(agent.getPlayerID());
        boolean bridgeExists = Arrays.stream(validRoads != null ? validRoads : new int[0]).anyMatch(e -> e == 1);
        Assumptions.assumeTrue(bridgeExists, "Edge 1 must be a valid bridge (empty and connectable)");
        Assumptions.assumeTrue(!board.getEdge(1).hasRoad(), "Bridge edge must be empty");

        AgentConstraintFacade facade = new AgentConstraintFacade(engine);
        ICommand result = facade.getPriorityConstraintAction(agent);

        assertNotNull(result);
        assertInstanceOf(BuildRoadCommand.class, result);
        assertEquals(1, ((BuildRoadCommand) result).getEdgeId(), "Bridge between edges 0 and 2 is edge 1");
    }

    @Test
    void longestRoadCompetitionTriggersConnectedRoadExtension() {
        agent.addResource(ResourceType.LUMBER, 1);
        agent.addResource(ResourceType.BRICK, 1);

        board.getNode(1).buildSettlement(agent);
        board.getEdge(0).buildRoad(agent);
        board.getEdge(1).buildRoad(agent);

        board.getNode(4).buildSettlement(rival);
        board.getEdge(2).buildRoad(rival);
        board.getEdge(3).buildRoad(rival);
        board.getEdge(4).buildRoad(rival);

        int L_agent = RoadGraphMetrics.longestRoadLength(board, topology, agent.getPlayerID());
        int L_rival = RoadGraphMetrics.longestRoadLength(board, topology, rival.getPlayerID());
        Assumptions.assumeTrue(L_rival >= L_agent - 1, "Rival must be within 1 of agent's longest road");

        int[] validRoads = engine.getValidRoadLocations(agent.getPlayerID());
        Assumptions.assumeTrue(validRoads != null && validRoads.length > 0, "Agent needs valid road locations");

        AgentConstraintFacade facade = new AgentConstraintFacade(engine);
        ICommand result = facade.getPriorityConstraintAction(agent);

        assertNotNull(result);
        assertInstanceOf(BuildRoadCommand.class, result);
        int edgeId = ((BuildRoadCommand) result).getEdgeId();
        assertTrue(Arrays.stream(engine.getValidRoadLocations(agent.getPlayerID())).anyMatch(e -> e == edgeId),
                "Returned edge must be in valid road locations");
    }
}
