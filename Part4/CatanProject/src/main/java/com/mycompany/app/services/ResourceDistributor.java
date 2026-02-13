package com.mycompany.app.services;

import com.mycompany.app.*;
import java.util.List;

/**
 * Handles resource distribution based on dice rolls
 * Implements Single Responsibility Principle - handles only resource distribution
 */
public class ResourceDistributor {
	private Board board;
	private IBoardGraph topology;

	public ResourceDistributor(Board board, IBoardGraph topology) {
		this.board = board;
		this.topology = topology;
	}

	/**
	 * Distribute resources based on dice roll to all players
	 * @param diceRoll The number rolled (2-12)
	 * @param players List of all players
	 */
	public void distribute(int diceRoll, List<Player> players) {
		// Don't distribute on 7 (robber)
		if (diceRoll == 7) {
			return;
		}

		// For each tile with matching number token
		for (HexTile tile : board.getAllTiles()) {
			if (tile.getNumberToken() == diceRoll &&
				tile.getType() != TileType.DESERT) {

				ResourceType resourceType = tile.getType().getResourceType();
				if (resourceType == null) continue;

				// Get nodes adjacent to this tile
				int[] tileNodes = topology.getTileNodes(tile.getTileID());

				// For each node, check if there's a building
				for (int nodeID : tileNodes) {
					Node node = board.getNode(nodeID);
					if (node != null && node.getOccupant() != null) {
						Player player = node.getOccupant();
						int amount = (node.getType() == BuildingType.CITY) ? 2 : 1;
						player.addResource(resourceType, amount);
					}
				}
			}
		}
	}
}
