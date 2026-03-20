package com.mycompany.app;

/**
 * Concrete Iterator for command history (Iterator pattern).
 * Owns the cursor; traversal state lives here, not in CommandHistory.
 */
public class HistoryIterator implements IHistoryIterator {

    private final CommandHistory history;
    private int cursor;

    HistoryIterator(CommandHistory history) {
        this.history = history;
        this.cursor = 0;
    }

    @Override
    public boolean hasPrevious() {
        return cursor > 0;
    }

    @Override
    public ICommand getPrevious() {
        if (!hasPrevious()) {
            return null;
        }
        cursor--;
        return history.get(cursor);
    }

    @Override
    public boolean hasNext() {
        return cursor < history.size();
    }

    @Override
    public ICommand getNext() {
        if (!hasNext()) {
            return null;
        }
        ICommand cmd = history.get(cursor);
        cursor++;
        return cmd;
    }

    @Override
    public int getPosition() {
        return cursor;
    }

    @Override
    public void advance() {
        cursor++;
    }
}
