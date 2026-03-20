package com.mycompany.app;

/**
 * Bidirectional iterator over command history (Iterator pattern).
 * Owns traversal state (cursor); clients use this instead of collection internals.
 */
public interface IHistoryIterator {

    boolean hasPrevious();

    /**
     * Move cursor back and return the command at that position (for undo).
     */
    ICommand getPrevious();

    boolean hasNext();

    /**
     * Return the command at current cursor and move cursor forward (for redo).
     */
    ICommand getNext();

    /**
     * Current cursor position (0 = nothing executed, size = all executed).
     */
    int getPosition();

    /**
     * Advance cursor by 1 (called after push).
     */
    void advance();
}
