package com.mycompany.app;

import com.mycompany.app.commands.*;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex-based parser for human CLI input.
 */
public class HumanInputParser implements IParser {

    private static final Pattern ROLL =
            Pattern.compile("^roll\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern END =
            Pattern.compile("^(end(\\s+turn)?|go)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_ROAD =
            Pattern.compile("^build\\s+road\\s+(\\d+)\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_SETTLEMENT =
            Pattern.compile("^build\\s+settlement\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_CITY =
            Pattern.compile("^build\\s+city\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ROBBER =
            Pattern.compile("^robber\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DISCARD =
            Pattern.compile("^discard\\s+(\\d+)\\s+(\\w+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUY_DEVCARD =
            Pattern.compile("^buy\\s+devcard\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PLAY_KNIGHT =
            Pattern.compile("^play\\s+knight\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PLAY_MONOPOLY =
            Pattern.compile("^play\\s+monopoly\\s+(\\w+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PLAY_ROADBUILDING =
            Pattern.compile("^play\\s+roadbuilding\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*$",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern PLAY_YEAROFPLENTY =
            Pattern.compile("^play\\s+yearofplenty\\s+(\\w+)\\s+(\\w+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PLAY_VICTORYPOINT =
            Pattern.compile("^play\\s+victorypoint\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern STATUS =
            Pattern.compile("^status\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern HELP =
            Pattern.compile("^help\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern UNDO =
            Pattern.compile("^undo\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern REDO =
            Pattern.compile("^redo\\s*$", Pattern.CASE_INSENSITIVE);

    private final IHistoryIterator iterator;

    /**
     * Create parser with iterator for undo/redo support.
     */
    public HumanInputParser(IHistoryIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public ICommand parse(String input) {
        if (input == null) {
            return new InvalidCommand("Empty command.");
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return new InvalidCommand("Empty command.");
        }

        Matcher m;

        m = ROLL.matcher(trimmed);
        if (m.matches()) {
            return new RollCommand();
        }

        m = END.matcher(trimmed);
        if (m.matches()) {
            return new EndTurnCommand();
        }

        m = BUILD_ROAD.matcher(trimmed);
        if (m.matches()) {
            int v1 = Integer.parseInt(m.group(1));
            int v2 = Integer.parseInt(m.group(2));
            // For now we simply encode the first vertex as the edge id
            // placeholder; a future enhancement can map (v1,v2) to edge.
            return new BuildRoadCommand(v1);
        }

        m = BUILD_SETTLEMENT.matcher(trimmed);
        if (m.matches()) {
            int nodeId = Integer.parseInt(m.group(1));
            return new BuildSettlementCommand(nodeId);
        }

        m = BUILD_CITY.matcher(trimmed);
        if (m.matches()) {
            int nodeId = Integer.parseInt(m.group(1));
            return new BuildCityCommand(nodeId);
        }

        m = ROBBER.matcher(trimmed);
        if (m.matches()) {
            int tileId = Integer.parseInt(m.group(1));
            return new RobberCommand(tileId);
        }

        m = DISCARD.matcher(trimmed);
        if (m.matches()) {
            int amount = Integer.parseInt(m.group(1));
            ResourceType type = parseResource(m.group(2));
            if (type == null) {
                return new InvalidCommand("Unknown resource type in discard command.");
            }
            return new DiscardCommand(amount, type);
        }

        m = BUY_DEVCARD.matcher(trimmed);
        if (m.matches()) {
            return new BuyDevCardCommand();
        }

        m = PLAY_KNIGHT.matcher(trimmed);
        if (m.matches()) {
            int tileId = Integer.parseInt(m.group(1));
            return new InvalidCommand("play knight " + tileId + " not implemented.");
        }

        m = PLAY_MONOPOLY.matcher(trimmed);
        if (m.matches()) {
            return new InvalidCommand("play monopoly not implemented.");
        }

        m = PLAY_ROADBUILDING.matcher(trimmed);
        if (m.matches()) {
            return new InvalidCommand("play roadbuilding not implemented.");
        }

        m = PLAY_YEAROFPLENTY.matcher(trimmed);
        if (m.matches()) {
            return new InvalidCommand("play yearofplenty not implemented.");
        }

        m = PLAY_VICTORYPOINT.matcher(trimmed);
        if (m.matches()) {
            return new InvalidCommand("play victorypoint not implemented.");
        }

        m = STATUS.matcher(trimmed);
        if (m.matches()) {
            return new StatusCommand();
        }

        m = HELP.matcher(trimmed);
        if (m.matches()) {
            return new HelpCommand();
        }

        m = UNDO.matcher(trimmed);
        if (m.matches()) {
            return new UndoCommand(iterator);
        }

        m = REDO.matcher(trimmed);
        if (m.matches()) {
            return new RedoCommand(iterator);
        }

        return new InvalidCommand("Unrecognized command: '" + input + "'");
    }

    private ResourceType parseResource(String token) {
        String normalized = token.toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "lumber":
            case "wood":
                return ResourceType.LUMBER;
            case "brick":
                return ResourceType.BRICK;
            case "grain":
            case "wheat":
                return ResourceType.GRAIN;
            case "wool":
            case "sheep":
                return ResourceType.WOOL;
            case "ore":
                return ResourceType.ORE;
            default:
                return null;
        }
    }
}

