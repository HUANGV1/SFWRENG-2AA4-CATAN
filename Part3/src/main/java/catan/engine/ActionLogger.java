package catan.engine;

import catan.player.Player;

import java.util.List;

/**
 * Logs game actions to standard output.
 */
public class ActionLogger {

    /**
     * Log a game action.
     *
     * @param round    the current round number
     * @param playerID the player performing the action
     * @param action   description of the action
     */
    public void logAction(int round, int playerID, String action) {
        System.out.println(round + " / Player " + playerID + ": " + action);
    }

    /**
     * Log a round summary showing VP for all players.
     */
    public void logRoundSummary(int round, List<Player> players) {
        StringBuilder sb = new StringBuilder();
        sb.append(round).append(" / VP Summary: ");
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            sb.append("Player ").append(p.getPlayerID()).append("=").append(p.getVictoryPoints()).append("VP");
            if (i < players.size() - 1)
                sb.append(", ");
        }
        System.out.println(sb);
    }
}
