package catan.interfaces;

import java.util.List;

/**
 * Interface for the game controller that players interact with.
 * Provides methods for querying valid locations and requesting builds.
 */
public interface IGameController {
    /**
     * @return list of node IDs where the player can place a settlement
     */
    List<Integer> getValidSettlementLocations(int playerID);

    /**
     * @return list of edge IDs where the player can place a road
     */
    List<Integer> getValidRoadLocations(int playerID);

    /**
     * Request to build a settlement at the given node.
     * 
     * @return true if the build was successful
     */
    boolean requestBuildSettlement(int playerID, int nodeID);

    /**
     * Request to build a road at the given edge.
     * 
     * @return true if the build was successful
     */
    boolean requestBuildRoad(int playerID, int edgeID);
}
