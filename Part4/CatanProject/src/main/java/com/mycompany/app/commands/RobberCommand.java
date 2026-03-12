package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.Player;

/**
 * Placeholder command for moving the robber.
 *
 * The current engine implementation models the robber only implicitly
 * (roll of 7 prevents distribution). This command is provided so that
 * the parser has a concrete target; its execute method currently just
 * logs the intent.
 */
public class RobberCommand implements ICommand {

    private final int tileId;

    public RobberCommand(int tileId) {
        this.tileId = tileId;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        System.out.println("Player " + currentPlayer.getPlayerID()
                + " requested robber move to tile " + tileId
                + " (robber movement not implemented)");
    }
}

