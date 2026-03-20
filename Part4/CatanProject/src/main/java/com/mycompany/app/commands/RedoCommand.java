package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.IHistoryIterator;
import com.mycompany.app.Player;

/**
 * Iterator Client: uses IHistoryIterator only. Triggers redo of the next command.
 */
public class RedoCommand implements ICommand {

    private final IHistoryIterator iterator;

    public RedoCommand(IHistoryIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        if (!iterator.hasNext()) {
            System.out.println("Nothing to redo.");
            return;
        }
        ICommand cmd = iterator.getNext();
        cmd.execute(controller, currentPlayer);
    }
}
