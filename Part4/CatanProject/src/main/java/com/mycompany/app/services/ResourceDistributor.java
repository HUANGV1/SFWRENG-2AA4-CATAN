package com.mycompany.app.services;

import com.mycompany.app.*;
import java.util.*;

/**
 * Handles resource distribution based on dice rolls
 * Implements Single Responsibility Principle - handles only resource
 * distribution
 */
public class ResourceDistributor {
	private Board board;
	private IBoardGraph topology;
	private Random random;

	public ResourceDistributor(Board board, IBoardGraph topology) {
		this.board = board;
		this.topology = topology;
		this.random = new Random();
	}

	/**
	 * Distribute resources based on dice roll to all players.
	 * Skips distribution for the tile where the robber is located.
	 * 
	 * @param diceRoll The number rolled (2-12)
	 * @param players  List of all players
	 */
	public void distribute(int diceRoll, List<Player> players) {
		// Don't distribute on 7 (robber)
		if (diceRoll == 7) {
			return;
		}

		// For each tile with matching number token
		for (HexTile tile : board.getAllTiles()) {
			// Skip the tile where the robber is located
			if (tile.getTileID() == board.getRobberLocation()) {
				continue;
			}

			if (tile.getNumberToken() == diceRoll &&
					tile.getType() != TileType.DESERT) {

				ResourceType resourceType = tile.getType().getResourceType();
				if (resourceType == null)
					continue;

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

	/**
	 * Handle the robber sequence: move robber to a random tile and steal a card.
	 * 
	 * @param activePlayer The player who rolled the 7
	 */
	public void handleRobber(Player activePlayer) {
		// Generate a random tile ID between 0 and 18 inclusive
		int newRobberTileID = random.nextInt(19);
		board.setRobberLocation(newRobberTileID);
		System.out.println("Robber moved to tile " + newRobberTileID);

		// Identify victims: players adjacent to the new robber tile
		int[] adjacentNodes = topology.getTileNodes(newRobberTileID);
		Set<Player> potentialVictims = new HashSet<>();

		for (int nodeID : adjacentNodes) {
			Node node = board.getNode(nodeID);
			if (node != null && node.getOccupant() != null) {
				Player occupant = node.getOccupant();
				// Exclude the active player and players with 0 resources
				if (occupant.getPlayerID() != activePlayer.getPlayerID()
						&& occupant.getTotalResourceCards() > 0) {
					potentialVictims.add(occupant);
				}
			}
		}

		// If there are valid targets, steal from a random one
		if (!potentialVictims.isEmpty()) {
			List<Player> victimList = new ArrayList<>(potentialVictims);
			Player victim = victimList.get(random.nextInt(victimList.size()));
			ResourceType stolen = victim.stealRandomResource();

			if (stolen != null) {
				activePlayer.addResource(stolen, 1);
				System.out.println("Player " + activePlayer.getPlayerID()
						+ " stole 1 " + stolen + " from Player " + victim.getPlayerID());
			}
		} else {
			System.out.println("No valid targets for robber theft.");
		}
	}

	/**
	 * Handle the over-seven-cards phase for all players.
	 * Each player with more than 7 cards must discard half (rounded down).
	 * 
	 * @param players List of all players
	 */
	public void handleOverSevenCardsPhase(List<Player> players) {
		for (Player player : players) {
			if (player.getTotalResourceCards() > 7) {
				int amountToDrop = player.getTotalResourceCards() / 2;
				System.out.println("Player " + player.getPlayerID() + " has "
						+ player.getTotalResourceCards() + " cards, must discard " + amountToDrop);
				player.robberDiscard(amountToDrop);
			}
		}
	}
}
