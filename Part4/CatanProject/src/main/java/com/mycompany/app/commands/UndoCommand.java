package com.mycompany.app.commands;

import com.mycompany.app.ICommand;
import com.mycompany.app.IGameController;
import com.mycompany.app.IHistoryIterator;
import com.mycompany.app.Player;

/**
 * Iterator Client: uses IHistoryIterator only. Triggers undo of the previous command.
 */
public class UndoCommand implements ICommand {

    private final IHistoryIterator iterator;

    public UndoCommand(IHistoryIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public void execute(IGameController controller, Player currentPlayer) {
        if (!iterator.hasPrevious()) {
            System.out.println("Nothing to undo.");
            return;
        }
        ICommand cmd = iterator.getPrevious();
        cmd.undo(controller, currentPlayer);
    }
}
