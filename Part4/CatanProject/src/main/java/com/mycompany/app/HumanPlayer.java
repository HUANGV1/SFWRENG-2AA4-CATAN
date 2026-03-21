package com.mycompany.app;

import com.mycompany.app.commands.*;

import java.util.Scanner;

/**
 * Human-controlled player that issues commands via the command line.
 * Invoker for Command pattern; creates CommandHistory and HistoryIterator for undo/redo.
 */
public class HumanPlayer extends Player {

	private enum TurnState {
		PRE_ROLL,
		WAIT_FOR_ROLL_RESOLUTION,
		ROBBER_PHASE,
		ACTION_PHASE
	}

	private final CommandHistory history = new CommandHistory();
	private final IHistoryIterator iterator = history.createIterator();
	private final IParser parser = new HumanInputParser(iterator);
	private final Scanner scanner;

	public HumanPlayer(int playerID, Scanner scanner) {
		super(playerID);
		this.scanner = scanner;
	}

	@Override
	public void takeTurn(IGameController controller) {
		System.out.println("Human player " + playerID + " turn. Type 'help' for commands.");

		TurnState state = TurnState.PRE_ROLL;
		while (true) {
			switch (state) {
				case PRE_ROLL:
					// State 1: pre-roll, only meaningful transition is to roll dice.
					System.out.print("[pre-roll] > ");
					ICommand preRollCommand = readCommand();
					if (preRollCommand == null) {
						return;
					}

					if (preRollCommand instanceof RollCommand) {
						preRollCommand.execute(controller, this);
						state = TurnState.WAIT_FOR_ROLL_RESOLUTION;
					} else if (preRollCommand instanceof HelpCommand ||
							preRollCommand instanceof StatusCommand ||
							preRollCommand instanceof InvalidCommand) {
						preRollCommand.execute(controller, this);
						// Remain in PRE_ROLL until a roll actually occurs.
					} else if (preRollCommand instanceof EndTurnCommand) {
						System.out.println("You must roll before ending your turn.");
					} else {
						System.out.println("You must roll first. Type 'roll' to roll the dice.");
					}
					break;

				case WAIT_FOR_ROLL_RESOLUTION:
					// State 2: in this implementation, roll resolution (including 7/robber)
					// is handled synchronously inside the RollCommand and engine,
					// so we immediately transition into the robber phase.
					state = TurnState.ROBBER_PHASE;
					break;

				case ROBBER_PHASE:
					// State 3: robber phase is driven by the engine via callbacks
					// (e.g., robberDiscard), so from the HumanPlayer's state
					// machine perspective we epsilon-transition to the action phase.
					state = TurnState.ACTION_PHASE;
					break;

				case ACTION_PHASE:
					// State 4: main action phase. The only stateful transition we
					// enforce here is: once you successfully invoke a build-oriented
					// command, the turn ends.
					System.out.print("[action] > ");
					ICommand actionCommand = readCommand();
					if (actionCommand == null) {
						return;
					}

					actionCommand.execute(controller, this);

					// Push successful build commands to history for undo/redo
					if (actionCommand.wasSuccessful() &&
							(actionCommand instanceof BuildSettlementCommand ||
							 actionCommand instanceof BuildRoadCommand ||
							 actionCommand instanceof BuildCityCommand)) {
						history.push(actionCommand, iterator.getPosition());
						iterator.advance();
					}

					// Only explicit end-turn ends the turn. Build commands stay in
					// action phase so the player can undo/redo before ending.
					if (actionCommand instanceof EndTurnCommand) {
						return;
					}
					// Otherwise, remain in ACTION_PHASE to allow further commands.
					break;
			}
		}
	}

	private ICommand readCommand() {
		if (!scanner.hasNextLine()) {
			return null;
		}
		String line = scanner.nextLine();
		if (line == null) {
			return null;
		}
		ICommand command = parser.parse(line);
		if (command == null) {
			return new InvalidCommand("Unrecognized command.");
		}
		return command;
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

