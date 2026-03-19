package com.mycompany.app;

import java.util.HashSet;
import java.util.Set;

/**
 * Computes longest contiguous road length for a player on the Catan board graph,
 * respecting opponent buildings as blockers (aligned with RoadValidator).
 */
public final class RoadGraphMetrics {

    private RoadGraphMetrics() {
    }

    public static int longestRoadLength(Board board, IBoardGraph topology, int playerId) {
        return longestRoadLength(board, topology, playerId, -1);
    }

    /**
     * As longestRoadLength but treats hypotheticalEdgeId as if the player owned it
     * (for prediction without mutating state). Pass -1 for no hypothetical edge.
     */
    public static int longestRoadLength(Board board, IBoardGraph topology, int playerId, int hypotheticalEdgeId) {
        int best = 0;
        for (Edge edge : board.getAllEdges()) {
            if (!isPlayerRoad(board, edge.getEdgeID(), playerId, hypotheticalEdgeId)) {
                continue;
            }
            Set<Integer> visited = new HashSet<>();
            visited.add(edge.getEdgeID());
            best = Math.max(best, extendRoad(board, topology, playerId, hypotheticalEdgeId, edge.getEdgeID(), visited));
        }
        return best;
    }

    private static boolean isPlayerRoad(Board board, int edgeId, int playerId, int hypotheticalEdgeId) {
        Edge edge = board.getEdge(edgeId);
        if (edge == null) {
            return false;
        }
        if (edge.hasRoadBy(playerId)) {
            return true;
        }
        return edgeId == hypotheticalEdgeId && !edge.hasRoad();
    }

    private static boolean isNodeBlockedForPlayer(Board board, int nodeId, int playerId) {
        Node node = board.getNode(nodeId);
        if (node == null) {
            return true;
        }
        if (!node.isOccupied()) {
            return false;
        }
        return node.getOccupant().getPlayerID() != playerId;
    }

    private static int extendRoad(Board board, IBoardGraph topology, int playerId, int hypotheticalEdgeId,
                                  int lastEdgeId, Set<Integer> visited) {
        int best = visited.size();
        int[] endpoints = topology.getEdgeEndpoints(lastEdgeId);
        if (endpoints == null || endpoints.length < 2) {
            return best;
        }
        for (int nodeId : endpoints) {
            if (isNodeBlockedForPlayer(board, nodeId, playerId)) {
                continue;
            }
            int[] adjacentEdges = topology.getAdjacentEdges(nodeId);
            for (int nextEdgeId : adjacentEdges) {
                if (visited.contains(nextEdgeId)) {
                    continue;
                }
                if (!isPlayerRoad(board, nextEdgeId, playerId, hypotheticalEdgeId)) {
                    continue;
                }
                visited.add(nextEdgeId);
                best = Math.max(best, extendRoad(board, topology, playerId, hypotheticalEdgeId, nextEdgeId, visited));
                visited.remove(nextEdgeId);
            }
        }
        return best;
    }
}
