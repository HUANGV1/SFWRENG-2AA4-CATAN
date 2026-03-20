package com.mycompany.app;

import com.mycompany.app.commands.BuildRoadCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandHistoryTest {

    private CommandHistory history;
    private IHistoryIterator iterator;

    @BeforeEach
    void setUp() {
        history = new CommandHistory();
        iterator = history.createIterator();
    }

    @Test
    void pushAndUndoViaIterator() {
        ICommand cmd = new BuildRoadCommand(5);
        history.push(cmd, iterator.getPosition());
        iterator.advance();

        assertTrue(iterator.hasPrevious());
        ICommand undone = iterator.getPrevious();
        assertNotNull(undone);
        assertEquals(0, iterator.getPosition());
    }

    @Test
    void undoAndRedoViaIterator() {
        ICommand cmd = new BuildRoadCommand(5);
        history.push(cmd, iterator.getPosition());
        iterator.advance();

        assertTrue(iterator.hasPrevious());
        iterator.getPrevious();
        assertEquals(0, iterator.getPosition());

        assertTrue(iterator.hasNext());
        ICommand redone = iterator.getNext();
        assertNotNull(redone);
        assertEquals(1, iterator.getPosition());
    }

    @Test
    void pushAfterUndoClearsBranch() {
        ICommand a = new BuildRoadCommand(0);
        ICommand b = new BuildRoadCommand(1);
        history.push(a, iterator.getPosition());
        iterator.advance();
        history.push(b, iterator.getPosition());
        iterator.advance();

        iterator.getPrevious();
        assertEquals(1, iterator.getPosition());

        ICommand c = new BuildRoadCommand(2);
        history.push(c, iterator.getPosition());
        iterator.advance();

        assertFalse(iterator.hasNext(), "Redo branch should be cleared after push");
    }

    @Test
    void cannotGoPastStart() {
        assertFalse(iterator.hasPrevious());
        assertNull(iterator.getPrevious());
    }

    @Test
    void cannotGoPastEnd() {
        ICommand cmd = new BuildRoadCommand(0);
        history.push(cmd, iterator.getPosition());
        iterator.advance();

        iterator.getPrevious();
        assertNotNull(iterator.getNext());
        assertFalse(iterator.hasNext(), "After redo, no more to redo");
        assertNull(iterator.getNext());
    }
}
