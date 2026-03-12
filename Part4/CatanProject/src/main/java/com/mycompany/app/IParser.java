package com.mycompany.app;

/**
 * Parses raw human input strings into executable game commands.
 *
 * Implementations are free to use any parsing strategy (e.g., regular
 * expressions) as long as they return an ICommand for every
 * non-null input string.
 */
public interface IParser {

    /**
     * Parse a human-issued command line into a concrete ICommand.
     *
     * @param input raw input line from the command line (may be null or empty)
     * @return a non-null command object; invalid or unrecognized input should
     *         result in an ICommand that reports the error without mutating
     *         game state
     */
    ICommand parse(String input);
}

