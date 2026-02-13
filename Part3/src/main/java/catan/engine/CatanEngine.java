package catan.engine;

import catan.board.Board;
import catan.enums.BuildingCost;
import catan.interfaces.IBoardGraph;
import catan.interfaces.IGameController;
import catan.interfaces.IRandomDice;
import catan.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Core game engine implementing the IGameController interface.
 * Delegates to SettlementValidator, RoadValidator, ResourceDistributor,
 * and BuildingService following the Single Responsibility Principle.
 */
public class CatanEngine implements IGameController {
    private final Board board;
    private final IRandomDice dice;
    private List<Player> players;
    private final SettlementValidator settlementValidator;
    private final RoadValidator roadValidator;
    private final ResourceDistributor resourceDistributor;
    private final BuildingService buildingService;

    public CatanEngine(Board board, IRandomDice dice) {
        this.board = board;
        this.dice = dice;
        this.players = new ArrayList<>();

        IBoardGraph topology = board.getTopology();
        this.settlementValidator = new SettlementValidator(board);
        this.roadValidator = new RoadValidator(board, topology);
        this.resourceDistributor = new ResourceDistributor(board, topology);
        this.buildingService = new BuildingService(board, settlementValidator, roadValidator);
    }

    /**
     * Set the list of players participating in the game.
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Roll the dice.
     * 
     * @return the dice result (2-12)
     */
    public int rollDice() {
        return dice.roll();
    }

    /**
     * Distribute resources to all players based on the dice roll.
     */
    public void distributeResources(int diceRoll, List<Player> players) {
        resourceDistributor.distribute(diceRoll, players);
    }

    @Override
    public List<Integer> getValidSettlementLocations(int playerID) {
        // During regular play, require road connection
        return settlementValidator.getValidLocations(playerID, false);
    }

    /**
     * Get valid settlement locations with explicit setup phase control.
     */
    public List<Integer> getValidSettlementLocations(int playerID, boolean isSetupPhase) {
        return settlementValidator.getValidLocations(playerID, isSetupPhase);
    }

    @Override
    public List<Integer> getValidRoadLocations(int playerID) {
        return roadValidator.getValidLocations(playerID);
    }

    @Override
    public boolean requestBuildSettlement(int playerID, int nodeID) {
        Player player = getPlayer(playerID);
        if (player == null)
            return false;
        return buildingService.buildSettlement(playerID, nodeID, player, false);
    }

    /**
     * Request to build a settlement (with setup phase option).
     */
    public boolean requestBuildSettlement(int playerID, int nodeID, boolean isSetup) {
        Player player = getPlayer(playerID);
        if (player == null)
            return false;
        return buildingService.buildSettlement(playerID, nodeID, player, isSetup);
    }

    @Override
    public boolean requestBuildRoad(int playerID, int edgeID) {
        Player player = getPlayer(playerID);
        if (player == null)
            return false;
        return buildingService.buildRoad(playerID, edgeID, player, false);
    }

    /**
     * Request to build a road (with setup phase option).
     */
    public boolean requestBuildRoad(int playerID, int edgeID, boolean isSetup) {
        Player player = getPlayer(playerID);
        if (player == null)
            return false;
        return buildingService.buildRoad(playerID, edgeID, player, isSetup);
    }

    /**
     * Request to upgrade a settlement to a city.
     */
    public boolean requestBuildCity(int playerID, int nodeID) {
        Player player = getPlayer(playerID);
        if (player == null)
            return false;
        return buildingService.buildCity(playerID, nodeID, player);
    }

    /**
     * Find a player by their ID.
     */
    public Player getPlayer(int playerID) {
        for (Player p : players) {
            if (p.getPlayerID() == playerID) {
                return p;
            }
        }
        return null;
    }

    public Board getBoard() {
        return board;
    }

    public SettlementValidator getSettlementValidator() {
        return settlementValidator;
    }

    public RoadValidator getRoadValidator() {
        return roadValidator;
    }
}
