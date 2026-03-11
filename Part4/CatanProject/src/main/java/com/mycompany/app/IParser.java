package com.mycompany.app;

/************************************************************/
/**
 * Parser interface for parsing string input into commands.
 */
public interface IParser {
    /**
     * Parse a string input into an ICommand.
     * 
     * @param input The raw string input to parse
     * @return The parsed ICommand
     */
    public ICommand parse(String input);
}
