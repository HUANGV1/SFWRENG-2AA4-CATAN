package com.mycompany.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Collection for command history (Iterator pattern).
 * Stores executed commands; cursor and traversal live in HistoryIterator.
 */
public class CommandHistory implements ICommandCollection {

    private final List<ICommand> commands = new ArrayList<>();

    @Override
    public IHistoryIterator createIterator() {
        return new HistoryIterator(this);
    }

    @Override
    public void push(ICommand cmd, int fromCursor) {
        if (fromCursor < commands.size()) {
            commands.subList(fromCursor, commands.size()).clear();
        }
        commands.add(cmd);
    }

    /**
     * Package-level access for HistoryIterator.
     */
    ICommand get(int index) {
        return commands.get(index);
    }

    /**
     * Package-level access for HistoryIterator.
     */
    int size() {
        return commands.size();
    }
}
