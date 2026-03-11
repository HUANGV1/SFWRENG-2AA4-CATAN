package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;

/**
 * Command returned when the parser cannot recognize the input.
 * Executing this command only reports the error and does not mutate
 * any game state.
 */
public class InvalidCommand implements ICommand {

    private final String message;

    public InvalidCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        System.out.println(message);
    }
}

