package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;

/**
 * Prints a short help message describing the supported CLI commands.
 */
public class HelpCommand implements ICommand {

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        System.out.println("Available commands:");
        System.out.println("  roll");
        System.out.println("  build road <vertex1> <vertex2>");
        System.out.println("  build settlement <vertex>");
        System.out.println("  build city <vertex>");
        System.out.println("  robber <tile>");
        System.out.println("  discard <amount> <resource>");
        System.out.println("  buy devcard");
        System.out.println("  play knight <tile>");
        System.out.println("  play monopoly <resource>");
        System.out.println("  play roadbuilding <v1> <v2> <v3> <v4>");
        System.out.println("  play yearofplenty <resource1> <resource2>");
        System.out.println("  play victorypoint");
        System.out.println("  status");
        System.out.println("  end | end turn | go");
    }
}

