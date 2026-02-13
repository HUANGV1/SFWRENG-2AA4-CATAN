package catan.simulator;

import catan.board.Board;
import catan.board.CatanBoardGraph;
import catan.engine.ActionLogger;
import catan.engine.CatanEngine;
import catan.engine.StandardDice;
import catan.enums.BuildingType;
import catan.enums.ResourceType;
import catan.enums.TileType;
import catan.interfaces.IBoardGraph;
import catan.interfaces.IRandomDice;
import catan.model.HexTile;
import catan.model.Node;
import catan.player.Player;
import catan.player.RandomAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Orchestrates a full Catan simulation.
 *
 * Termination: game ends when a player reaches 10 VP or the round limit is
 * reached.
 * Setup uses snake draft order (1-2-3-4-4-3-2-1).
 */
public class Simulator {
    private static final int VICTORY_POINTS_TO_WIN = 10;
    private static final int NUM_PLAYERS = 4;

    private final CatanEngine engine;
    private final List<Player> players;
    private int curRounds;
    private final int maxRounds;
    private final ActionLogger logger;
    private Player winner;

    public Simulator(int maxRounds) {
        this(maxRounds, System.currentTimeMillis());
    }

    public Simulator(int maxRounds, long seed) {
        if (maxRounds < 1) {
            throw new IllegalArgumentException("Round limit must be at least 1");
        }
        this.maxRounds = maxRounds;

        IBoardGraph topology = new CatanBoardGraph();
        Board board = new Board(topology, seed);
        IRandomDice dice = new StandardDice(seed);

        this.engine = new CatanEngine(board, dice);
        this.logger = new ActionLogger();
        this.players = new ArrayList<>();
        this.curRounds = 0;
        this.winner = null;

        for (int i = 0; i < NUM_PLAYERS; i++) {
            players.add(new RandomAgent(i + 1, seed + i));
        }
        engine.setPlayers(players);
    }

    /**
     * Run the setup phase: snake draft (1-2-3-4-4-3-2-1).
     */
    public void initialSetup() {
        logger.logAction(0, 0, "Setup phase - Snake draft order (1-2-3-4-4-3-2-1)");

        // Forward order
        for (int i = 0; i < NUM_PLAYERS; i++) {
            RandomAgent agent = (RandomAgent) players.get(i);
            // Place settlement
            List<Integer> validNodes = engine.getValidSettlementLocations(agent.getPlayerID(), true);
            if (!validNodes.isEmpty()) {
                int nodeId = validNodes
                        .get(new java.util.Random(agent.getPlayerID() * 31L + curRounds).nextInt(validNodes.size()));
                engine.requestBuildSettlement(agent.getPlayerID(), nodeId, true);
                logger.logAction(0, agent.getPlayerID(), "Placed settlement at node " + nodeId);

                // Place road adjacent to settlement
                IBoardGraph topology = engine.getBoard().getTopology();
                List<Integer> adjEdges = topology.getAdjacentEdges(nodeId);
                for (int edgeId : adjEdges) {
                    if (!engine.getBoard().getEdge(edgeId).hasRoad()) {
                        engine.requestBuildRoad(agent.getPlayerID(), edgeId, true);
                        logger.logAction(0, agent.getPlayerID(), "Placed road at edge " + edgeId);
                        break;
                    }
                }
            }
        }

        // Reverse order (second settlement + road)
        for (int i = NUM_PLAYERS - 1; i >= 0; i--) {
            RandomAgent agent = (RandomAgent) players.get(i);
            List<Integer> validNodes = engine.getValidSettlementLocations(agent.getPlayerID(), true);
            if (!validNodes.isEmpty()) {
                int nodeId = validNodes
                        .get(new java.util.Random(agent.getPlayerID() * 71L + curRounds).nextInt(validNodes.size()));
                engine.requestBuildSettlement(agent.getPlayerID(), nodeId, true);
                logger.logAction(0, agent.getPlayerID(), "Placed settlement at node " + nodeId);

                // Place road
                IBoardGraph topology = engine.getBoard().getTopology();
                List<Integer> adjEdges = topology.getAdjacentEdges(nodeId);
                for (int edgeId : adjEdges) {
                    if (!engine.getBoard().getEdge(edgeId).hasRoad()) {
                        engine.requestBuildRoad(agent.getPlayerID(), edgeId, true);
                        logger.logAction(0, agent.getPlayerID(), "Placed road at edge " + edgeId);
                        break;
                    }
                }

                // Give starting resources from second settlement's adjacent tiles
                giveStartingResources(agent, nodeId);
            }
        }
    }

    private void giveStartingResources(Player player, int settlementNodeId) {
        IBoardGraph topology = engine.getBoard().getTopology();
        Board board = engine.getBoard();

        // Find tiles adjacent to this node
        for (int tileId = 0; tileId < 19; tileId++) {
            List<Integer> tileNodes = topology.getTileNodes(tileId);
            if (tileNodes.contains(settlementNodeId)) {
                HexTile tile = board.getTile(tileId);
                if (tile != null) {
                    ResourceType resource = tile.getType().toResourceType();
                    if (resource != null) {
                        player.addResource(resource, 1);
                    }
                }
            }
        }
    }

    /**
     * Run the full game simulation.
     */
    public void runSimulation() {
        initialSetup();

        while (curRounds < maxRounds && winner == null) {
            curRounds++;

            for (Player player : players) {
                // Roll dice
                int roll = engine.rollDice();
                logger.logAction(curRounds, player.getPlayerID(), "Rolled " + roll);

                if (roll == 7) {
                    // Robber: no resources, players with >7 cards discard half
                    logger.logAction(curRounds, player.getPlayerID(),
                            "Robber activated (rolled 7) - no resources distributed");
                    handleRobber();
                } else {
                    engine.distributeResources(roll, players);
                }

                // Player takes turn
                player.takeTurn(engine);

                // Check for winner
                if (player.getVictoryPoints() >= VICTORY_POINTS_TO_WIN) {
                    winner = player;
                    break;
                }
            }

            // Print VP summary
            logger.logRoundSummary(curRounds, players);

            if (winner != null)
                break;
        }

        printFinalScores();
    }

    private void handleRobber() {
        for (Player player : players) {
            if (player.getTotalResourceCount() > 7) {
                int cardsBefore = player.getTotalResourceCount();
                player.handleOverSevenCards();
                int discarded = cardsBefore - player.getTotalResourceCount();
                logger.logAction(curRounds, player.getPlayerID(),
                        "Discarded " + discarded + " cards");
            }
        }
    }

    /**
     * Print round scores (VP summary).
     */
    public void printRoundScores() {
        logger.logRoundSummary(curRounds, players);
    }

    /**
     * Print the final game results.
     */
    public void printFinalScores() {
        System.out.println("\n===========================================");
        System.out.println("  FINAL RESULTS");
        System.out.println("===========================================\n");

        if (winner != null) {
            System.out.println("GAME OVER: Player " + winner.getPlayerID()
                    + " wins with " + winner.getVictoryPoints() + " VP after "
                    + curRounds + " rounds");
        } else {
            System.out.println("Game ended after " + curRounds + " rounds (no winner)");
        }

        System.out.println("\nWinner: "
                + (winner != null ? "Player " + winner.getPlayerID() + " with " + winner.getVictoryPoints() + " VP"
                        : "None"));
        System.out.println("Total rounds: " + curRounds);
        System.out.println("\nFinal standings:");
        for (Player p : players) {
            System.out.println("  Player " + p.getPlayerID() + ": "
                    + p.getVictoryPoints() + " VP, "
                    + p.getTotalResourceCount() + " resources");
        }
    }

    public CatanEngine getEngine() {
        return engine;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getCurRounds() {
        return curRounds;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public Player getWinner() {
        return winner;
    }
}
