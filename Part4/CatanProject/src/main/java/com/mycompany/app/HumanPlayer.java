package com.mycompany.app;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/************************************************************/
/**
 * Human-controlled player that reads commands from the console.
 * Uses HumanInputParser for regex-based command parsing.
 */
public class HumanPlayer extends Player {
    /**
     * Scanner for reading console input
     */
    private Scanner scanner;

    /**
     * Parser for interpreting human commands
     */
    private HumanInputParser parser;

    /**
     * Constructor for HumanPlayer
     * 
     * @param playerID Unique player identifier
     */
    public HumanPlayer(int playerID) {
        this(playerID, new Scanner(System.in));
    }

    /**
     * Constructor for HumanPlayer with a shared Scanner
     * 
     * @param playerID Unique player identifier
     * @param scanner  Shared Scanner for reading input
     */
    public HumanPlayer(int playerID, Scanner scanner) {
        super(playerID);
        this.scanner = scanner;
        this.parser = new HumanInputParser();
    }

    /**
     * Take a turn by reading commands from the console.
     * Loops until the player types "done" or "end".
     * 
     * @param controller Game controller interface for making moves
     */
    @Override
    public void takeTurn(IGameController controller) {
        System.out.println("\n--- Player " + playerID + "'s turn (Human) ---");
        System.out.println("Commands: Roll, List, Build settlement <id>, Build road <id>, Build city <id>, Done");

        boolean turnOver = false;
        while (!turnOver && scanner.hasNextLine()) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            ICommand command = parser.parse(input);

            switch (command.getType()) {
                case "ROLL":
                    System.out.println("Dice have already been rolled by the engine this turn.");
                    break;

                case "LIST":
                    printResources();
                    break;

                case "BUILD_SETTLEMENT":
                    boolean settlementSuccess = controller.requestBuildSettlement(playerID, command.getArgument());
                    if (settlementSuccess) {
                        System.out.println("Settlement built at node " + command.getArgument());
                    } else {
                        System.out.println("Cannot build settlement at node " + command.getArgument());
                    }
                    break;

                case "BUILD_ROAD":
                    boolean roadSuccess = controller.requestBuildRoad(playerID, command.getArgument());
                    if (roadSuccess) {
                        System.out.println("Road built at edge " + command.getArgument());
                    } else {
                        System.out.println("Cannot build road at edge " + command.getArgument());
                    }
                    break;

                case "BUILD_CITY":
                    if (controller instanceof CatanEngine) {
                        boolean citySuccess = ((CatanEngine) controller).requestBuildCity(playerID,
                                command.getArgument());
                        if (citySuccess) {
                            System.out.println("City built at node " + command.getArgument());
                        } else {
                            System.out.println("Cannot build city at node " + command.getArgument());
                        }
                    }
                    break;

                case "DONE":
                    turnOver = true;
                    System.out.println("Turn ended.");
                    break;

                case "INVALID":
                    System.out.println("Invalid command. Try: Roll, List, Build settlement/road/city <id>, Done");
                    break;

                default:
                    System.out.println("Unrecognized command: " + command.getType());
                    break;
            }
        }
    }

    /**
     * Handle having more than 7 cards.
     */
    @Override
    public void handleOverSevenCards() {
        // Empty - robber discard logic is in robberDiscard()
    }

    /**
     * Prompt the user to discard cards when the robber is activated.
     * Reads from console and validates the total matches amountToDrop.
     * 
     * @param amountToDrop The number of cards the player must discard
     */
    @Override
    public void robberDiscard(int amountToDrop) {
        System.out.println("\n--- Robber Discard ---");
        System.out.println("You must discard " + amountToDrop + " cards.");
        printResources();
        System.out.println("Enter resources to discard (e.g., 'LUMBER 2 BRICK 1'):");

        // Pattern to match pairs of RESOURCE_NAME and AMOUNT
        Pattern discardPattern = Pattern.compile("(?i)(LUMBER|BRICK|GRAIN|WOOL|ORE)\\s+(\\d+)");

        while (scanner.hasNextLine()) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            Matcher matcher = discardPattern.matcher(input);
            Map<ResourceType, Integer> toDiscard = new HashMap<>();
            int totalDiscarding = 0;

            while (matcher.find()) {
                String resourceName = matcher.group(1).toUpperCase();
                int amount = Integer.parseInt(matcher.group(2));
                ResourceType type = ResourceType.valueOf(resourceName);
                toDiscard.put(type, toDiscard.getOrDefault(type, 0) + amount);
                totalDiscarding += amount;
            }

            // Validate total
            if (totalDiscarding != amountToDrop) {
                System.out.println("You must discard exactly " + amountToDrop + " cards. You specified "
                        + totalDiscarding + ". Try again.");
                continue;
            }

            // Validate player has enough of each resource
            boolean valid = true;
            for (Map.Entry<ResourceType, Integer> entry : toDiscard.entrySet()) {
                if (getResourceCount(entry.getKey()) < entry.getValue()) {
                    System.out.println("You don't have enough " + entry.getKey() + ". Try again.");
                    valid = false;
                    break;
                }
            }

            if (valid) {
                // Deduct the resources
                for (Map.Entry<ResourceType, Integer> entry : toDiscard.entrySet()) {
                    deductResource(entry.getKey(), entry.getValue());
                }
                System.out.println("Discarded " + amountToDrop + " cards.");
                break;
            }
        }
    }

    /**
     * Print the player's current resources to console
     */
    private void printResources() {
        System.out.println("Your resources:");
        Map<ResourceType, Integer> res = getResources();
        for (ResourceType type : ResourceType.values()) {
            System.out.println("  " + type + ": " + res.get(type));
        }
        System.out.println("  Total: " + getTotalResourceCards() + " cards");
    }
}
