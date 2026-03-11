package com.mycompany.app;

/**
 * Represents a parsed command from user input.
 */
public interface ICommand {
    /**
     * Get the type of this command (e.g., "ROLL", "GO", "LIST", "BUILD_SETTLEMENT",
     * etc.)
     * 
     * @return The command type as a string
     */
    public String getType();

    /**
     * Get the argument associated with this command (e.g., node/edge ID).
     * 
     * @return The argument value, or -1 if no argument
     */
    public int getArgument();
}
