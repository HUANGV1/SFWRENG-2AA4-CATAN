package com.mycompany.app;

/**
 * Collection interface for command history (Iterator pattern).
 * Exposes createIterator() and push; traversal state lives in the iterator.
 */
public interface ICommandCollection {

    /**
     * Create a new iterator over this collection.
     */
    IHistoryIterator createIterator();

    /**
     * Append a command, truncating any redo branch from fromCursor onward.
     *
     * @param cmd        the command to add
     * @param fromCursor truncate elements at this index and beyond before adding
     */
    void push(ICommand cmd, int fromCursor);
}
