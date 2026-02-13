package catan.board;

import catan.enums.TileType;
import catan.interfaces.IBoardGraph;
import catan.model.Edge;
import catan.model.HexTile;
import catan.model.Node;

import java.util.*;

/**
 * Represents the physical Catan board, holding tiles, nodes, and edges.
 * Accepts an IBoardGraph for topology (Dependency Inversion).
 */
public class Board {
    private final Map<Integer, HexTile> tiles;
    private final Map<Integer, Node> nodes;
    private final Map<Integer, Edge> edges;
    private final IBoardGraph topology;

    public Board(IBoardGraph topology) {
        this.topology = topology;
        this.tiles = new HashMap<>();
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        initializeBoard();
    }

    public Board(IBoardGraph topology, long seed) {
        this.topology = topology;
        this.tiles = new HashMap<>();
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        initializeBoard(seed);
    }

    private void initializeBoard() {
        initializeBoard(System.currentTimeMillis());
    }

    private void initializeBoard(long seed) {
        // Create 54 nodes
        for (int i = 0; i < 54; i++) {
            nodes.put(i, new Node(i));
        }

        // Create 72 edges
        for (int i = 0; i < 72; i++) {
            edges.put(i, new Edge(i));
        }

        // Create 19 tiles with shuffled types and number tokens
        Random random = new Random(seed);

        List<TileType> tileTypes = new ArrayList<>();
        tileTypes.addAll(Collections.nCopies(4, TileType.WOOD));
        tileTypes.addAll(Collections.nCopies(3, TileType.BRICK));
        tileTypes.addAll(Collections.nCopies(4, TileType.SHEEP));
        tileTypes.addAll(Collections.nCopies(4, TileType.WHEAT));
        tileTypes.addAll(Collections.nCopies(3, TileType.ORE));
        tileTypes.add(TileType.DESERT);

        Collections.shuffle(tileTypes, random);

        List<Integer> numberTokens = new ArrayList<>();
        numberTokens.add(2);
        numberTokens.addAll(Collections.nCopies(2, 3));
        numberTokens.addAll(Collections.nCopies(2, 4));
        numberTokens.addAll(Collections.nCopies(2, 5));
        numberTokens.addAll(Collections.nCopies(2, 6));
        numberTokens.addAll(Collections.nCopies(2, 8));
        numberTokens.addAll(Collections.nCopies(2, 9));
        numberTokens.addAll(Collections.nCopies(2, 10));
        numberTokens.addAll(Collections.nCopies(2, 11));
        numberTokens.add(12);

        Collections.shuffle(numberTokens, random);

        int tokenIndex = 0;
        for (int tileId = 0; tileId < 19; tileId++) {
            TileType type = tileTypes.get(tileId);
            int numberToken = 0;
            if (type != TileType.DESERT) {
                numberToken = numberTokens.get(tokenIndex++);
            }
            tiles.put(tileId, new HexTile(tileId, type, numberToken));
        }
    }

    public Node getNode(int nodeID) {
        return nodes.get(nodeID);
    }

    public Edge getEdge(int edgeID) {
        return edges.get(edgeID);
    }

    public HexTile getTile(int tileID) {
        return tiles.get(tileID);
    }

    public Collection<HexTile> getAllTiles() {
        return tiles.values();
    }

    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    public Collection<Edge> getAllEdges() {
        return edges.values();
    }

    public IBoardGraph getTopology() {
        return topology;
    }

    /**
     * @return list of tiles that have the given number token
     */
    public List<HexTile> getTilesWithNumber(int number) {
        List<HexTile> result = new ArrayList<>();
        for (HexTile tile : tiles.values()) {
            if (tile.hasNumberToken() && tile.getNumberToken() == number) {
                result.add(tile);
            }
        }
        return result;
    }
}
