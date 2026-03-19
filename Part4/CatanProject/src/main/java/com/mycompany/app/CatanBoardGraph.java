package com.mycompany.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Hardcoded topology for a standard Catan board.
 * Matches the node/edge/tile topology computed by the Python visualizer
 * (base_map.json tile order = GameStateObserver.TILE_COORDS).
 *
 * Board structure:
 * - 19 tiles: 0 (center), 1-6 (inner ring), 7-18 (outer ring)
 * - 54 nodes: intersection points where settlements/cities can be built
 * - 72 edges: paths between nodes where roads can be built
 */
public class CatanBoardGraph implements IBoardGraph {
    // Node adjacency map: node ID -> array of adjacent node IDs
    private final Map<Integer, int[]> nodeAdjacencies;

    // Edge adjacency map: node ID -> array of adjacent edge IDs
    private final Map<Integer, int[]> edgeAdjacencies;

    // Edge endpoints map: edge ID -> array of two endpoint node IDs
    private final Map<Integer, int[]> edgeEndpoints;

    // Tile-to-node map: tile ID -> 6 surrounding node IDs [N, NE, SE, S, SW, NW]
    private final Map<Integer, int[]> tileNodes;

    /**
     * Constructor initializes the hardcoded Catan board topology
     */
    public CatanBoardGraph() {
        this.nodeAdjacencies = new HashMap<>();
        this.edgeAdjacencies = new HashMap<>();
        this.edgeEndpoints = new HashMap<>();
        this.tileNodes = new HashMap<>();

        initializeTopology();
    }

    /**
     * Initialize the board topology with hardcoded adjacencies matching the
     * Python visualizer's algorithm (base_map.json / TILE_COORDS order).
     */
    private void initializeTopology() {
        // Node adjacencies (54 nodes)
        nodeAdjacencies.put(0, new int[]{1, 5, 20});
        nodeAdjacencies.put(1, new int[]{0, 2, 6});
        nodeAdjacencies.put(2, new int[]{1, 3, 9});
        nodeAdjacencies.put(3, new int[]{2, 4, 12});
        nodeAdjacencies.put(4, new int[]{3, 5, 15});
        nodeAdjacencies.put(5, new int[]{0, 4, 16});
        nodeAdjacencies.put(6, new int[]{1, 7, 23});
        nodeAdjacencies.put(7, new int[]{6, 8, 24});
        nodeAdjacencies.put(8, new int[]{7, 9, 27});
        nodeAdjacencies.put(9, new int[]{2, 8, 10});
        nodeAdjacencies.put(10, new int[]{9, 11, 29});
        nodeAdjacencies.put(11, new int[]{10, 12, 32});
        nodeAdjacencies.put(12, new int[]{3, 11, 13});
        nodeAdjacencies.put(13, new int[]{12, 14, 34});
        nodeAdjacencies.put(14, new int[]{13, 15, 37});
        nodeAdjacencies.put(15, new int[]{4, 14, 17});
        nodeAdjacencies.put(16, new int[]{5, 18, 21});
        nodeAdjacencies.put(17, new int[]{15, 18, 39});
        nodeAdjacencies.put(18, new int[]{16, 17, 40});
        nodeAdjacencies.put(19, new int[]{20, 21, 46});
        nodeAdjacencies.put(20, new int[]{0, 19, 22});
        nodeAdjacencies.put(21, new int[]{16, 19, 43});
        nodeAdjacencies.put(22, new int[]{20, 23, 49});
        nodeAdjacencies.put(23, new int[]{6, 22, 52});
        nodeAdjacencies.put(24, new int[]{7, 25, 53});
        nodeAdjacencies.put(25, new int[]{24, 26});
        nodeAdjacencies.put(26, new int[]{25, 27});
        nodeAdjacencies.put(27, new int[]{8, 26, 28});
        nodeAdjacencies.put(28, new int[]{27, 29});
        nodeAdjacencies.put(29, new int[]{10, 28, 30});
        nodeAdjacencies.put(30, new int[]{29, 31});
        nodeAdjacencies.put(31, new int[]{30, 32});
        nodeAdjacencies.put(32, new int[]{11, 31, 33});
        nodeAdjacencies.put(33, new int[]{32, 34});
        nodeAdjacencies.put(34, new int[]{13, 33, 35});
        nodeAdjacencies.put(35, new int[]{34, 36});
        nodeAdjacencies.put(36, new int[]{35, 37});
        nodeAdjacencies.put(37, new int[]{14, 36, 38});
        nodeAdjacencies.put(38, new int[]{37, 39});
        nodeAdjacencies.put(39, new int[]{17, 38, 41});
        nodeAdjacencies.put(40, new int[]{18, 42, 44});
        nodeAdjacencies.put(41, new int[]{39, 42});
        nodeAdjacencies.put(42, new int[]{40, 41});
        nodeAdjacencies.put(43, new int[]{21, 44, 47});
        nodeAdjacencies.put(44, new int[]{40, 43});
        nodeAdjacencies.put(45, new int[]{46, 47});
        nodeAdjacencies.put(46, new int[]{19, 45, 48});
        nodeAdjacencies.put(47, new int[]{43, 45});
        nodeAdjacencies.put(48, new int[]{46, 49});
        nodeAdjacencies.put(49, new int[]{22, 48, 50});
        nodeAdjacencies.put(50, new int[]{49, 51});
        nodeAdjacencies.put(51, new int[]{50, 52});
        nodeAdjacencies.put(52, new int[]{23, 51, 53});
        nodeAdjacencies.put(53, new int[]{24, 52});

        // Edge endpoints (72 edges)
        edgeEndpoints.put(0, new int[]{1, 2});
        edgeEndpoints.put(1, new int[]{2, 3});
        edgeEndpoints.put(2, new int[]{3, 4});
        edgeEndpoints.put(3, new int[]{4, 5});
        edgeEndpoints.put(4, new int[]{5, 0});
        edgeEndpoints.put(5, new int[]{0, 1});
        edgeEndpoints.put(6, new int[]{7, 8});
        edgeEndpoints.put(7, new int[]{8, 9});
        edgeEndpoints.put(8, new int[]{9, 2});
        edgeEndpoints.put(9, new int[]{1, 6});
        edgeEndpoints.put(10, new int[]{6, 7});
        edgeEndpoints.put(11, new int[]{9, 10});
        edgeEndpoints.put(12, new int[]{10, 11});
        edgeEndpoints.put(13, new int[]{11, 12});
        edgeEndpoints.put(14, new int[]{12, 3});
        edgeEndpoints.put(15, new int[]{12, 13});
        edgeEndpoints.put(16, new int[]{13, 14});
        edgeEndpoints.put(17, new int[]{14, 15});
        edgeEndpoints.put(18, new int[]{15, 4});
        edgeEndpoints.put(19, new int[]{15, 17});
        edgeEndpoints.put(20, new int[]{17, 18});
        edgeEndpoints.put(21, new int[]{18, 16});
        edgeEndpoints.put(22, new int[]{16, 5});
        edgeEndpoints.put(23, new int[]{20, 0});
        edgeEndpoints.put(24, new int[]{16, 21});
        edgeEndpoints.put(25, new int[]{21, 19});
        edgeEndpoints.put(26, new int[]{19, 20});
        edgeEndpoints.put(27, new int[]{23, 6});
        edgeEndpoints.put(28, new int[]{20, 22});
        edgeEndpoints.put(29, new int[]{22, 23});
        edgeEndpoints.put(30, new int[]{25, 26});
        edgeEndpoints.put(31, new int[]{26, 27});
        edgeEndpoints.put(32, new int[]{27, 8});
        edgeEndpoints.put(33, new int[]{7, 24});
        edgeEndpoints.put(34, new int[]{24, 25});
        edgeEndpoints.put(35, new int[]{27, 28});
        edgeEndpoints.put(36, new int[]{28, 29});
        edgeEndpoints.put(37, new int[]{29, 10});
        edgeEndpoints.put(38, new int[]{29, 30});
        edgeEndpoints.put(39, new int[]{30, 31});
        edgeEndpoints.put(40, new int[]{31, 32});
        edgeEndpoints.put(41, new int[]{32, 11});
        edgeEndpoints.put(42, new int[]{32, 33});
        edgeEndpoints.put(43, new int[]{33, 34});
        edgeEndpoints.put(44, new int[]{34, 13});
        edgeEndpoints.put(45, new int[]{34, 35});
        edgeEndpoints.put(46, new int[]{35, 36});
        edgeEndpoints.put(47, new int[]{36, 37});
        edgeEndpoints.put(48, new int[]{37, 14});
        edgeEndpoints.put(49, new int[]{37, 38});
        edgeEndpoints.put(50, new int[]{38, 39});
        edgeEndpoints.put(51, new int[]{39, 17});
        edgeEndpoints.put(52, new int[]{39, 41});
        edgeEndpoints.put(53, new int[]{41, 42});
        edgeEndpoints.put(54, new int[]{42, 40});
        edgeEndpoints.put(55, new int[]{40, 18});
        edgeEndpoints.put(56, new int[]{40, 44});
        edgeEndpoints.put(57, new int[]{44, 43});
        edgeEndpoints.put(58, new int[]{43, 21});
        edgeEndpoints.put(59, new int[]{46, 19});
        edgeEndpoints.put(60, new int[]{43, 47});
        edgeEndpoints.put(61, new int[]{47, 45});
        edgeEndpoints.put(62, new int[]{45, 46});
        edgeEndpoints.put(63, new int[]{49, 22});
        edgeEndpoints.put(64, new int[]{46, 48});
        edgeEndpoints.put(65, new int[]{48, 49});
        edgeEndpoints.put(66, new int[]{51, 52});
        edgeEndpoints.put(67, new int[]{52, 23});
        edgeEndpoints.put(68, new int[]{49, 50});
        edgeEndpoints.put(69, new int[]{50, 51});
        edgeEndpoints.put(70, new int[]{53, 24});
        edgeEndpoints.put(71, new int[]{52, 53});

        // Node-to-edge adjacencies (54 nodes)
        edgeAdjacencies.put(0, new int[]{4, 5, 23});
        edgeAdjacencies.put(1, new int[]{0, 5, 9});
        edgeAdjacencies.put(2, new int[]{0, 1, 8});
        edgeAdjacencies.put(3, new int[]{1, 2, 14});
        edgeAdjacencies.put(4, new int[]{2, 3, 18});
        edgeAdjacencies.put(5, new int[]{3, 4, 22});
        edgeAdjacencies.put(6, new int[]{9, 10, 27});
        edgeAdjacencies.put(7, new int[]{6, 10, 33});
        edgeAdjacencies.put(8, new int[]{6, 7, 32});
        edgeAdjacencies.put(9, new int[]{7, 8, 11});
        edgeAdjacencies.put(10, new int[]{11, 12, 37});
        edgeAdjacencies.put(11, new int[]{12, 13, 41});
        edgeAdjacencies.put(12, new int[]{13, 14, 15});
        edgeAdjacencies.put(13, new int[]{15, 16, 44});
        edgeAdjacencies.put(14, new int[]{16, 17, 48});
        edgeAdjacencies.put(15, new int[]{17, 18, 19});
        edgeAdjacencies.put(16, new int[]{21, 22, 24});
        edgeAdjacencies.put(17, new int[]{19, 20, 51});
        edgeAdjacencies.put(18, new int[]{20, 21, 55});
        edgeAdjacencies.put(19, new int[]{25, 26, 59});
        edgeAdjacencies.put(20, new int[]{23, 26, 28});
        edgeAdjacencies.put(21, new int[]{24, 25, 58});
        edgeAdjacencies.put(22, new int[]{28, 29, 63});
        edgeAdjacencies.put(23, new int[]{27, 29, 67});
        edgeAdjacencies.put(24, new int[]{33, 34, 70});
        edgeAdjacencies.put(25, new int[]{30, 34});
        edgeAdjacencies.put(26, new int[]{30, 31});
        edgeAdjacencies.put(27, new int[]{31, 32, 35});
        edgeAdjacencies.put(28, new int[]{35, 36});
        edgeAdjacencies.put(29, new int[]{36, 37, 38});
        edgeAdjacencies.put(30, new int[]{38, 39});
        edgeAdjacencies.put(31, new int[]{39, 40});
        edgeAdjacencies.put(32, new int[]{40, 41, 42});
        edgeAdjacencies.put(33, new int[]{42, 43});
        edgeAdjacencies.put(34, new int[]{43, 44, 45});
        edgeAdjacencies.put(35, new int[]{45, 46});
        edgeAdjacencies.put(36, new int[]{46, 47});
        edgeAdjacencies.put(37, new int[]{47, 48, 49});
        edgeAdjacencies.put(38, new int[]{49, 50});
        edgeAdjacencies.put(39, new int[]{50, 51, 52});
        edgeAdjacencies.put(40, new int[]{54, 55, 56});
        edgeAdjacencies.put(41, new int[]{52, 53});
        edgeAdjacencies.put(42, new int[]{53, 54});
        edgeAdjacencies.put(43, new int[]{57, 58, 60});
        edgeAdjacencies.put(44, new int[]{56, 57});
        edgeAdjacencies.put(45, new int[]{61, 62});
        edgeAdjacencies.put(46, new int[]{59, 62, 64});
        edgeAdjacencies.put(47, new int[]{60, 61});
        edgeAdjacencies.put(48, new int[]{64, 65});
        edgeAdjacencies.put(49, new int[]{63, 65, 68});
        edgeAdjacencies.put(50, new int[]{68, 69});
        edgeAdjacencies.put(51, new int[]{66, 69});
        edgeAdjacencies.put(52, new int[]{66, 67, 71});
        edgeAdjacencies.put(53, new int[]{70, 71});

        // Tile-to-node mapping (19 tiles): [N, NE, SE, S, SW, NW]
        tileNodes.put(0, new int[]{0, 1, 2, 3, 4, 5});
        tileNodes.put(1, new int[]{6, 7, 8, 9, 2, 1});
        tileNodes.put(2, new int[]{2, 9, 10, 11, 12, 3});
        tileNodes.put(3, new int[]{4, 3, 12, 13, 14, 15});
        tileNodes.put(4, new int[]{16, 5, 4, 15, 17, 18});
        tileNodes.put(5, new int[]{19, 20, 0, 5, 16, 21});
        tileNodes.put(6, new int[]{22, 23, 6, 1, 0, 20});
        tileNodes.put(7, new int[]{24, 25, 26, 27, 8, 7});
        tileNodes.put(8, new int[]{8, 27, 28, 29, 10, 9});
        tileNodes.put(9, new int[]{10, 29, 30, 31, 32, 11});
        tileNodes.put(10, new int[]{12, 11, 32, 33, 34, 13});
        tileNodes.put(11, new int[]{14, 13, 34, 35, 36, 37});
        tileNodes.put(12, new int[]{17, 15, 14, 37, 38, 39});
        tileNodes.put(13, new int[]{40, 18, 17, 39, 41, 42});
        tileNodes.put(14, new int[]{43, 21, 16, 18, 40, 44});
        tileNodes.put(15, new int[]{45, 46, 19, 21, 43, 47});
        tileNodes.put(16, new int[]{48, 49, 22, 20, 19, 46});
        tileNodes.put(17, new int[]{50, 51, 52, 23, 22, 49});
        tileNodes.put(18, new int[]{52, 53, 24, 7, 6, 23});
    }

    @Override
    public int[] getAdjacentNodes(int nodeID) {
        return nodeAdjacencies.getOrDefault(nodeID, new int[]{});
    }

    @Override
    public int[] getAdjacentEdges(int nodeID) {
        return edgeAdjacencies.getOrDefault(nodeID, new int[]{});
    }

    @Override
    public int[] getEdgeEndpoints(int edgeID) {
        return edgeEndpoints.getOrDefault(edgeID, new int[]{});
    }

    @Override
    public int[] getTileNodes(int tileID) {
        return tileNodes.getOrDefault(tileID, new int[]{});
    }
}
