package catan.engine;

import catan.board.Board;
import catan.enums.BuildingCost;
import catan.enums.BuildingType;
import catan.enums.ResourceType;
import catan.model.Edge;
import catan.model.Node;
import catan.player.Player;

import java.util.Map;

/**
 * Handles building actions: validates placement, deducts resources,
 * and places structures on the board.
 * (Single Responsibility Principle)
 */
public class BuildingService {
    private final Board board;
    private final SettlementValidator settlementValidator;
    private final RoadValidator roadValidator;

    public BuildingService(Board board, SettlementValidator settlementValidator,
            RoadValidator roadValidator) {
        this.board = board;
        this.settlementValidator = settlementValidator;
        this.roadValidator = roadValidator;
    }

    /**
     * Build a settlement at the given node for the specified player.
     *
     * @param playerID the ID of the player building
     * @param nodeID   the node to build on
     * @param player   the player object
     * @param isSetup  true during setup phase (no cost)
     * @return true if the settlement was built successfully
     */
    public boolean buildSettlement(int playerID, int nodeID, Player player, boolean isSetup) {
        if (!settlementValidator.isValid(playerID, nodeID, isSetup)) {
            return false;
        }

        if (!isSetup) {
            if (!player.hasResources(BuildingCost.SETTLEMENT.getCost())) {
                return false;
            }
            deductResources(player, BuildingCost.SETTLEMENT.getCost());
        }

        Node node = board.getNode(nodeID);
        node.buildSettlement(player);
        player.addVictoryPoints(1);
        return true;
    }

    /**
     * Build a road at the given edge for the specified player.
     *
     * @param playerID the ID of the player building
     * @param edgeID   the edge to build on
     * @param player   the player object
     * @param isSetup  true during setup phase (no cost)
     * @return true if the road was built successfully
     */
    public boolean buildRoad(int playerID, int edgeID, Player player, boolean isSetup) {
        Edge edge = board.getEdge(edgeID);
        if (edge == null || edge.hasRoad()) {
            return false;
        }

        if (!isSetup) {
            // During regular play, must connect to player's network
            if (!roadValidator.isValid(playerID, edgeID)) {
                return false;
            }
        }

        if (!isSetup) {
            if (!player.hasResources(BuildingCost.ROAD.getCost())) {
                return false;
            }
            deductResources(player, BuildingCost.ROAD.getCost());
        }

        edge.buildRoad(playerID, player);
        return true;
    }

    /**
     * Upgrade a settlement to a city at the given node.
     *
     * @param playerID the ID of the player building
     * @param nodeID   the node to upgrade
     * @param player   the player object
     * @return true if the city was built successfully
     */
    public boolean buildCity(int playerID, int nodeID, Player player) {
        Node node = board.getNode(nodeID);
        if (node == null)
            return false;

        // Must be player's own settlement
        if (!node.hasSettlementBy(player)) {
            return false;
        }

        if (!player.hasResources(BuildingCost.CITY.getCost())) {
            return false;
        }

        deductResources(player, BuildingCost.CITY.getCost());
        node.upgradeToCity(player);
        player.addVictoryPoints(1); // +1 additional VP (settlement already gave 1)
        return true;
    }

    /**
     * Deduct resources from a player's hand.
     */
    private void deductResources(Player player, Map<ResourceType, Integer> cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            player.removeResource(entry.getKey(), entry.getValue());
        }
    }
}
