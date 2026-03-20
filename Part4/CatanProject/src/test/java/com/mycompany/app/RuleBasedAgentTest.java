package com.mycompany.app;

import com.mycompany.app.commands.BuildRoadCommand;
import com.mycompany.app.commands.BuildSettlementCommand;
import com.mycompany.app.commands.BuildCityCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RuleBasedAgentTest {

    private Board board;
    private CatanEngine engine;
    private RuleBasedAgent ruleBasedAgent;
    private RandomAgent randomAgent;

    @BeforeEach
    void setUp() {
        IBoardGraph topology = new CatanBoardGraph();
        board = new Board(topology);
        engine = new CatanEngine(board, () -> 4);
        ruleBasedAgent = new RuleBasedAgent(0, engine);
        randomAgent = new RandomAgent(1);
        engine.setPlayers(List.of(ruleBasedAgent, randomAgent));
    }

    @Test
    void vpCommandsScoreOne() {
        assertEquals(1.0, ruleBasedAgent.evaluateCommand(new BuildSettlementCommand(0)));
        assertEquals(1.0, ruleBasedAgent.evaluateCommand(new BuildCityCommand(0)));
    }

    @Test
    void roadCommandScoresZeroEight() {
        assertEquals(0.8, ruleBasedAgent.evaluateCommand(new BuildRoadCommand(5)));
    }

    @Test
    void chooseBestActionPrefersVpOverRoad() {
        board.getNode(0).buildSettlement(ruleBasedAgent);
        ruleBasedAgent.addResource(ResourceType.LUMBER, 2);
        ruleBasedAgent.addResource(ResourceType.BRICK, 2);
        ruleBasedAgent.addResource(ResourceType.GRAIN, 1);
        ruleBasedAgent.addResource(ResourceType.WOOL, 1);

        List<ICommand> candidates = new ArrayList<>();
        candidates.add(new BuildRoadCommand(5));
        candidates.add(new BuildSettlementCommand(1));

        ICommand chosen = ruleBasedAgent.chooseBestAction(candidates);
        assertInstanceOf(BuildSettlementCommand.class, chosen);
    }

    @Test
    void chooseBestActionRandomTieBreaker() {
        List<ICommand> candidates = new ArrayList<>();
        candidates.add(new BuildRoadCommand(5));
        candidates.add(new BuildRoadCommand(13));

        Set<Integer> seenEdgeIds = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            ICommand chosen = ruleBasedAgent.chooseBestAction(candidates);
            assertInstanceOf(BuildRoadCommand.class, chosen);
            seenEdgeIds.add(((BuildRoadCommand) chosen).getEdgeId());
        }
        assertEquals(2, seenEdgeIds.size(), "Both edges should appear over 100 runs");
    }

    @Test
    void randomAgentEvaluateCommandAlwaysZero() {
        assertEquals(0.0, randomAgent.evaluateCommand(new BuildSettlementCommand(0)));
        assertEquals(0.0, randomAgent.evaluateCommand(new BuildRoadCommand(0)));
    }
}
