package catan.board;

import catan.interfaces.IBoardGraph;

import java.util.*;

/**
 * Hardcoded topology for the standard Catan board.
 *
 * 19 hex tiles (IDs 0-18), 54 node intersections (IDs 0-53), 72 edge/road
 * segments (IDs 0-71).
 *
 * Node rows (top to bottom):
 * Row A (3): 0 1 2
 * Row B (4): 3 4 5 6
 * Row C (4): 7 8 9 10
 * Row D (5): 11 12 13 14 15
 * Row E (5): 16 17 18 19 20
 * Row F (6): 21 22 23 24 25 26
 * Row G (6): 27 28 29 30 31 32
 * Row H (5): 33 34 35 36 37
 * Row I (5): 38 39 40 41 42
 * Row J (4): 43 44 45 46
 * Row K (4): 47 48 49 50
 * Row L (3): 51 52 53
 *
 * Tile rows (3-4-5-4-3):
 * Row 1: T0 T1 T2
 * Row 2: T3 T4 T5 T6
 * Row 3: T7 T8 T9 T10 T11
 * Row 4: T12 T13 T14 T15
 * Row 5: T16 T17 T18
 *
 * Each tile lists its 6 corner nodes clockwise from the top vertex.
 */
public class CatanBoardGraph implements IBoardGraph {

    private final Map<Integer, List<Integer>> nodeAdjacencies;
    private final Map<Integer, List<Integer>> edgeAdjacencies;
    private final Map<Integer, List<Integer>> edgeEndpoints;

    // Additional maps for full topology support
    private final Map<Integer, List<Integer>> nodeToEdges;
    private final Map<Integer, List<Integer>> tileToNodes;

    public CatanBoardGraph() {
        nodeAdjacencies = new HashMap<>();
        edgeAdjacencies = new HashMap<>();
        edgeEndpoints = new HashMap<>();
        nodeToEdges = new HashMap<>();
        tileToNodes = new HashMap<>();

        initializeTopology();
    }

    private void initializeTopology() {
        for (int i = 0; i < 54; i++) {
            nodeAdjacencies.put(i, new ArrayList<>());
            nodeToEdges.put(i, new ArrayList<>());
        }

        // Top row (3 hexes)
        tileToNodes.put(0, Arrays.asList(0, 4, 8, 12, 7, 3));
        tileToNodes.put(1, Arrays.asList(1, 5, 9, 13, 8, 4));
        tileToNodes.put(2, Arrays.asList(2, 6, 10, 14, 9, 5));

        // Second row (4 hexes)
        tileToNodes.put(3, Arrays.asList(7, 12, 17, 22, 16, 11));
        tileToNodes.put(4, Arrays.asList(8, 13, 18, 23, 17, 12));
        tileToNodes.put(5, Arrays.asList(9, 14, 19, 24, 18, 13));
        tileToNodes.put(6, Arrays.asList(10, 15, 20, 25, 19, 14));

        // Third row (5 hexes — centre)
        tileToNodes.put(7, Arrays.asList(16, 22, 28, 33, 27, 21));
        tileToNodes.put(8, Arrays.asList(17, 23, 29, 34, 28, 22));
        tileToNodes.put(9, Arrays.asList(18, 24, 30, 35, 29, 23));
        tileToNodes.put(10, Arrays.asList(19, 25, 31, 36, 30, 24));
        tileToNodes.put(11, Arrays.asList(20, 26, 32, 37, 31, 25));

        // Fourth row (4 hexes)
        tileToNodes.put(12, Arrays.asList(28, 34, 39, 43, 38, 33));
        tileToNodes.put(13, Arrays.asList(29, 35, 40, 44, 39, 34));
        tileToNodes.put(14, Arrays.asList(30, 36, 41, 45, 40, 35));
        tileToNodes.put(15, Arrays.asList(31, 37, 42, 46, 41, 36));

        // Bottom row (3 hexes)
        tileToNodes.put(16, Arrays.asList(39, 44, 48, 51, 47, 43));
        tileToNodes.put(17, Arrays.asList(40, 45, 49, 52, 48, 44));
        tileToNodes.put(18, Arrays.asList(41, 46, 50, 53, 49, 45));

        // Build node adjacencies from tile corners
        for (List<Integer> tileNodes : tileToNodes.values()) {
            for (int i = 0; i < 6; i++) {
                int node1 = tileNodes.get(i);
                int node2 = tileNodes.get((i + 1) % 6);
                if (!nodeAdjacencies.get(node1).contains(node2)) {
                    nodeAdjacencies.get(node1).add(node2);
                }
                if (!nodeAdjacencies.get(node2).contains(node1)) {
                    nodeAdjacencies.get(node2).add(node1);
                }
            }
        }

        // Build edge mappings from node adjacencies
        int edgeId = 0;
        Set<String> processedEdges = new HashSet<>();

        for (int nodeId = 0; nodeId < 54; nodeId++) {
            List<Integer> adjacentNodes = nodeAdjacencies.get(nodeId);
            if (adjacentNodes != null) {
                for (int adjNode : adjacentNodes) {
                    String edgeKey = Math.min(nodeId, adjNode) + "-" + Math.max(nodeId, adjNode);
                    if (!processedEdges.contains(edgeKey)) {
                        processedEdges.add(edgeKey);
                        edgeEndpoints.put(edgeId, Arrays.asList(nodeId, adjNode));
                        nodeToEdges.get(nodeId).add(edgeId);
                        nodeToEdges.get(adjNode).add(edgeId);
                        addEdge(nodeId, edgeId);
                        addEdge(adjNode, edgeId);
                        edgeId++;
                    }
                }
            }
        }
    }

    /**
     * Associates a node with an edge for adjacency tracking.
     */
    private void addEdge(int nodeId, int edgeId) {
        edgeAdjacencies.computeIfAbsent(nodeId, k -> new ArrayList<>());
        if (!edgeAdjacencies.get(nodeId).contains(edgeId)) {
            edgeAdjacencies.get(nodeId).add(edgeId);
        }
    }

    @Override
    public List<Integer> getAdjacentNodes(int nodeID) {
        return nodeAdjacencies.getOrDefault(nodeID, Collections.emptyList());
    }

    @Override
    public List<Integer> getAdjacentEdges(int nodeID) {
        return nodeToEdges.getOrDefault(nodeID, Collections.emptyList());
    }

    @Override
    public List<Integer> getEdgeEndpoints(int edgeID) {
        return edgeEndpoints.getOrDefault(edgeID, Collections.emptyList());
    }

    @Override
    public List<Integer> getTileNodes(int tileID) {
        return tileToNodes.getOrDefault(tileID, Collections.emptyList());
    }

    public int getTotalNodes() {
        return 54;
    }

    public int getTotalEdges() {
        return edgeEndpoints.size();
    }

    public int getTotalTiles() {
        return 19;
    }
}
