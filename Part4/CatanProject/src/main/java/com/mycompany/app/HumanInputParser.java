package com.mycompany.app;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses human input strings into ICommand objects using regular expressions.
 * Implements IParser to provide regex-based command recognition.
 */
public class HumanInputParser implements IParser {
    /**
     * Map of command names to their regex patterns
     */
    public Map<String, Pattern> commandPatterns;

    /**
     * Constructor - initializes all regex command patterns
     */
    public HumanInputParser() {
        commandPatterns = new LinkedHashMap<>();
        commandPatterns.put("BUILD_SETTLEMENT", Pattern.compile("(?i)build\\s+settlement\\s+(\\d+)"));
        commandPatterns.put("BUILD_ROAD", Pattern.compile("(?i)build\\s+road\\s+(\\d+)"));
        commandPatterns.put("BUILD_CITY", Pattern.compile("(?i)build\\s+city\\s+(\\d+)"));
        commandPatterns.put("ROLL", Pattern.compile("(?i)roll"));
        commandPatterns.put("GO", Pattern.compile("(?i)go"));
        commandPatterns.put("LIST", Pattern.compile("(?i)list"));
        commandPatterns.put("DONE", Pattern.compile("(?i)done|end"));
    }

    /**
     * Parse a string input into an ICommand using regex pattern matching.
     * 
     * @param input The raw string input from the user
     * @return An ICommand representing the parsed command, or an INVALID command
     */
    @Override
    public ICommand parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return createCommand("INVALID", -1);
        }

        String trimmed = input.trim();

        for (Map.Entry<String, Pattern> entry : commandPatterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(trimmed);
            if (matcher.matches()) {
                String type = entry.getKey();
                int argument = -1;

                // Extract the numeric argument if present (group 1)
                if (matcher.groupCount() >= 1) {
                    try {
                        argument = Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException e) {
                        // No numeric argument, keep -1
                    }
                }

                return createCommand(type, argument);
            }
        }

        return createCommand("INVALID", -1);
    }

    /**
     * Creates an ICommand with the given type and argument.
     * 
     * @param type     The command type
     * @param argument The command argument (-1 if none)
     * @return A new ICommand instance
     */
    private ICommand createCommand(String type, int argument) {
        final String cmdType = type;
        final int cmdArg = argument;
        return new ICommand() {
            @Override
            public String getType() {
                return cmdType;
            }

            @Override
            public int getArgument() {
                return cmdArg;
            }
        };
    }
}
