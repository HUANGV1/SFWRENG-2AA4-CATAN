package com.mycompany.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Hardcoded topology for a standard Catan board.
 * Defines adjacency relationships between nodes and edges.
 *
 * Board structure:
 * - 19 tiles: 0 (center), 1-6 (inner ring), 7-18 (outer ring)
 * - 54 nodes: intersection points where settlements/cities can be built
 * - 72 edges: paths between nodes where roads can be built
 */
public class CatanBoardGraph implements IBoardGraph {
    // Node adjacency map: node ID -> array of adjacent node IDs
    private Map<Integer, int[]> nodeAdjacencies;

    // Edge adjacency map: node ID -> array of adjacent edge IDs
    private Map<Integer, int[]> edgeAdjacencies;

    // Edge endpoints map: edge ID -> array of two endpoint node IDs
    private Map<Integer, int[]> edgeEndpoints;

    /**
     * Constructor initializes the hardcoded Catan board topology
     */
    public CatanBoardGraph() {
        this.nodeAdjacencies = new HashMap<>();
        this.edgeAdjacencies = new HashMap<>();
        this.edgeEndpoints = new HashMap<>();

        initializeTopology();
    }

    /**
     * Initialize the board topology with hardcoded adjacencies.
     * This creates a simplified Catan-like hexagonal board structure.
     */
    private void initializeTopology() {
        // Initialize all 54 nodes with empty adjacencies first
        for (int i = 0; i < 54; i++) {
            nodeAdjacencies.put(i, new int[]{});
            edgeAdjacencies.put(i, new int[]{});
        }

        // Center hexagon nodes (nodes 0-5 around center tile)
        // Each node connects to 2 neighbors in clockwise order
        nodeAdjacencies.put(0, new int[]{1, 5});
        nodeAdjacencies.put(1, new int[]{0, 2});
        nodeAdjacencies.put(2, new int[]{1, 3});
        nodeAdjacencies.put(3, new int[]{2, 4});
        nodeAdjacencies.put(4, new int[]{3, 5});
        nodeAdjacencies.put(5, new int[]{4, 0});

        // Inner ring nodes (nodes 6-17, 2 per inner ring tile)
        // These connect to center nodes and to each other
        for (int i = 6; i < 18; i++) {
            int centerNode = (i - 6) / 2;  // Which center node this connects to
            int nextInner = i + 1;
            if (nextInner >= 18) nextInner = 6;
            int prevInner = i - 1;
            if (prevInner < 6) prevInner = 17;

            if (i % 2 == 0) {  // Even inner nodes
                nodeAdjacencies.put(i, new int[]{centerNode, nextInner});
            } else {  // Odd inner nodes
                nodeAdjacencies.put(i, new int[]{centerNode, nextInner});
            }
        }

        // Outer ring nodes (nodes 18-53, 3 per outer ring tile)
        // Simplified outer ring - each connects to 2 neighbors
        for (int i = 18; i < 54; i++) {
            int next = i + 1;
            if (next >= 54) next = 18;
            int prev = i - 1;
            if (prev < 18) prev = 53;

            if (i < 53) {
                nodeAdjacencies.put(i, new int[]{prev, next});
            } else {
                nodeAdjacencies.put(i, new int[]{prev, 18});  // Wrap around
            }
        }

        // Initialize edges
        // Each edge connects two nodes
        int edgeId = 0;

        // Create edges for center hexagon (6 edges)
        for (int i = 0; i < 6; i++) {
            int node1 = i;
            int node2 = (i + 1) % 6;
            edgeEndpoints.put(edgeId, new int[]{node1, node2});

            // Add this edge to both nodes' edge adjacency lists
            addEdgeToNode(node1, edgeId);
            addEdgeToNode(node2, edgeId);

            edgeId++;
        }

        // Create edges connecting center to inner ring (12 edges)
        for (int i = 0; i < 12; i++) {
            int centerNode = i / 2;
            int innerNode = 6 + i;
            edgeEndpoints.put(edgeId, new int[]{centerNode, innerNode});

            addEdgeToNode(centerNode, edgeId);
            addEdgeToNode(innerNode, edgeId);

            edgeId++;
        }

        // Create edges for inner ring (12 edges connecting inner ring nodes)
        for (int i = 6; i < 18; i++) {
            int nextNode = (i - 6 + 1) % 12 + 6;
            edgeEndpoints.put(edgeId, new int[]{i, nextNode});

            addEdgeToNode(i, edgeId);
            addEdgeToNode(nextNode, edgeId);

            edgeId++;
        }

        // Create remaining edges for outer ring (simplified)
        // This creates a basic structure; a full implementation would need all 72 edges
        for (int i = 18; i < 54 && edgeId < 72; i++) {
            int nextNode = (i - 18 + 1) % 36 + 18;
            edgeEndpoints.put(edgeId, new int[]{i, nextNode});

            addEdgeToNode(i, edgeId);
            addEdgeToNode(nextNode, edgeId);

            edgeId++;
        }
    }

    /**
     * Helper method to add an edge to a node's edge adjacency list
     */
    private void addEdgeToNode(int nodeId, int edgeId) {
        int[] currentEdges = edgeAdjacencies.get(nodeId);
        int[] newEdges = new int[currentEdges.length + 1];
        System.arraycopy(currentEdges, 0, newEdges, 0, currentEdges.length);
        newEdges[currentEdges.length] = edgeId;
        edgeAdjacencies.put(nodeId, newEdges);
    }

    @Override
    public int[] getAdjacentNodes(int nodeID) {
        return nodeAdjacencies.getOrDefault(nodeID, new int[]{});
    }

    @Override
    public int[] getAdjacentEdges(int nodeID) {
        return edgeAdjacencies.getOrDefault(nodeID, new int[]{});
    }

    /**
     * Get the two endpoint nodes of an edge
     * @param edgeID The edge ID
     * @return Array of two node IDs that are the endpoints of this edge
     */
    @Override
    public int[] getEdgeEndpoints(int edgeID) {
        return edgeEndpoints.getOrDefault(edgeID, new int[]{});
    }

    /**
     * Get nodes adjacent to a specific tile
     * @param tileID The tile ID (0-18)
     * @return Array of node IDs surrounding this tile
     */
    @Override
    public int[] getTileNodes(int tileID) {
        // Center tile (ID 0) has nodes 0-5
        if (tileID == 0) {
            return new int[]{0, 1, 2, 3, 4, 5};
        }

        // Inner ring tiles (1-6) - simplified
        if (tileID >= 1 && tileID <= 6) {
            int baseNode = (tileID - 1) * 2 + 6;
            return new int[]{
                (tileID - 1),
                tileID % 6,
                baseNode,
                baseNode + 1
            };
        }

        // Outer ring tiles (7-18) - simplified
        if (tileID >= 7 && tileID <= 18) {
            int baseNode = (tileID - 7) * 3 + 18;
            return new int[]{
                baseNode,
                baseNode + 1,
                baseNode + 2
            };
        }

        return new int[]{};
    }
}
