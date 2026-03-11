package com.mycompany.app;

import com.mycompany.app.commands.DiscardCommand;
import com.mycompany.app.commands.EndTurnCommand;
import com.mycompany.app.commands.HelpCommand;
import com.mycompany.app.commands.InvalidCommand;

import java.util.Scanner;

/**
 * Human-controlled player that issues commands via the command line.
 *
 * The HumanPlayer delegates parsing of raw input lines to an IParser
 * and executes the resulting ICommand objects
 * against the provided IGameController.
 */
public class HumanPlayer extends Player {

    private final IParser parser;
    private final Scanner scanner;

    public HumanPlayer(int playerID, IParser parser, Scanner scanner) {
        super(playerID);
        this.parser = parser;
        this.scanner = scanner;
    }

    @Override
    public void takeTurn(IGameController controller) {
        System.out.println("Human player " + playerID + " turn. Type 'help' for commands.");
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                // No more input; end the turn gracefully.
                return;
            }
            String line = scanner.nextLine();
            if (line == null) {
                return;
            }
            com.mycompany.app.ICommand command = parser.parse(line);
            if (command == null) {
                // Defensive: should never happen, but avoid NPE.
                command = new InvalidCommand("Unrecognized command.");
            }
            command.execute(controller, this);
            if (command instanceof EndTurnCommand) {
                return;
            }
        }
    }

    @Override
    public void handleOverSevenCards() {
        // Intentionally left blank for human players.
        // Robber-related discards are handled via robberDiscard(int).
    }

    @Override
    public void robberDiscard(int amountToDrop) {
        int remaining = amountToDrop;
        System.out.println("You must discard " + amountToDrop + " resource cards.");
        new HelpCommand().execute(null, this);
        while (remaining > 0 && getTotalResourceCards() > 0) {
            System.out.println("Remaining to discard: " + remaining);
            System.out.print("discard> ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String line = scanner.nextLine();
            com.mycompany.app.ICommand cmd = parser.parse(line);
            if (cmd instanceof DiscardCommand) {
                int before = getTotalResourceCards();
                cmd.execute(null, this);
                int after = getTotalResourceCards();
                int dropped = Math.max(0, before - after);
                remaining -= dropped;
            } else {
                System.out.println("Please use 'discard <amount> <resource>' during robber discard.");
            }
        }
    }

    @Override
    public ResourceType stealRandomResource() {
        // For a human player being robbed, we simply choose the first
        // resource with a non-zero count to keep behavior deterministic.
        for (ResourceType type : ResourceType.values()) {
            if (getResourceCount(type) > 0) {
                deductResource(type, 1);
                return type;
            }
        }
        return null;
    }
}

