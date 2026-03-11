package com.mycompany.app;

import com.mycompany.app.commands.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HumanInputParserTest {

    private final HumanInputParser parser = new HumanInputParser();

    @Test
    void testParseRoll() {
        ICommand cmd = parser.parse("roll");
        assertTrue(cmd instanceof RollCommand, "roll should produce RollCommand");
    }

    @Test
    void testParseBuildRoad() {
        ICommand cmd = parser.parse("build road 1 2");
        assertTrue(cmd instanceof BuildRoadCommand, "build road should produce BuildRoadCommand");
    }

    @Test
    void testParseBuildSettlement() {
        ICommand cmd = parser.parse("build settlement 5");
        assertTrue(cmd instanceof BuildSettlementCommand, "build settlement should produce BuildSettlementCommand");
    }

    @Test
    void testParseBuildCity() {
        ICommand cmd = parser.parse("build city 3");
        assertTrue(cmd instanceof BuildCityCommand, "build city should produce BuildCityCommand");
    }

    @Test
    void testParseDiscard() {
        ICommand cmd = parser.parse("discard 2 grain");
        assertTrue(cmd instanceof DiscardCommand, "discard should produce DiscardCommand");
        DiscardCommand discard = (DiscardCommand) cmd;
        assertEquals(2, discard.getAmount());
        assertEquals(ResourceType.GRAIN, discard.getResourceType());
    }

    @Test
    void testParseEndVariants() {
        assertTrue(parser.parse("end") instanceof EndTurnCommand, "end should end turn");
        assertTrue(parser.parse("end turn") instanceof EndTurnCommand, "end turn should end turn");
        assertTrue(parser.parse("go") instanceof EndTurnCommand, "go should end turn");
    }

    @Test
    void testParseStatusAndHelp() {
        assertTrue(parser.parse("status") instanceof StatusCommand, "status should produce StatusCommand");
        assertTrue(parser.parse("help") instanceof HelpCommand, "help should produce HelpCommand");
    }

    @Test
    void testParseInvalidCommand() {
        ICommand cmd = parser.parse("build potato");
        assertTrue(cmd instanceof InvalidCommand, "Unrecognized input should produce InvalidCommand");
    }
}

