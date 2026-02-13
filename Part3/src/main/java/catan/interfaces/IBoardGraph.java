package catan.interfaces;

import java.util.List;

/**
 * Abstraction for the board topology (Dependency Inversion Principle).
 * Decouples game logic from the specific board layout.
 */
public interface IBoardGraph {
    /**
     * @return list of node IDs adjacent to the given node
     */
    List<Integer> getAdjacentNodes(int nodeID);

    /**
     * @return list of edge IDs connected to the given node
     */
    List<Integer> getAdjacentEdges(int nodeID);

    /**
     * @return list of the two node IDs at the endpoints of the given edge
     */
    List<Integer> getEdgeEndpoints(int edgeID);

    /**
     * @return list of node IDs surrounding the given tile (hex corners)
     */
    List<Integer> getTileNodes(int tileID);
}
