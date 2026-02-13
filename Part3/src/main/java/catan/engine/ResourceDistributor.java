package catan.engine;

import catan.board.Board;
import catan.enums.BuildingType;
import catan.enums.ResourceType;
import catan.enums.TileType;
import catan.interfaces.IBoardGraph;
import catan.model.HexTile;
import catan.model.Node;
import catan.player.Player;

import java.util.List;

/**
 * Handles resource distribution based on dice rolls.
 * (Single Responsibility Principle)
 */
public class ResourceDistributor {
    private final Board board;
    private final IBoardGraph topology;

    public ResourceDistributor(Board board, IBoardGraph topology) {
        this.board = board;
        this.topology = topology;
    }

    /**
     * Distribute resources to all players based on the dice roll.
     * Each tile with the matching number token produces resources
     * for all adjacent settlements (1 resource) and cities (2 resources).
     *
     * @param diceRoll the sum of the two dice
     * @param players  list of all players in the game
     */
    public void distribute(int diceRoll, List<Player> players) {
        List<HexTile> activeTiles = board.getTilesWithNumber(diceRoll);

        for (HexTile tile : activeTiles) {
            TileType tileType = tile.getType();
            ResourceType resource = tileType.toResourceType();
            if (resource == null)
                continue; // Desert produces nothing

            List<Integer> tileNodes = topology.getTileNodes(tile.getTileID());
            for (int nodeId : tileNodes) {
                Node node = board.getNode(nodeId);
                if (node.isOccupied()) {
                    Player owner = node.getOccupant();
                    int amount = (node.getType() == BuildingType.CITY) ? 2 : 1;
                    owner.addResource(resource, amount);
                }
            }
        }
    }
}
