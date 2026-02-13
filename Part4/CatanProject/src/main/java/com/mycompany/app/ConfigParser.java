package com.mycompany.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Parser for reading configuration files for the Catan simulator
 */
public class ConfigParser {
    /**
     * Read the maximum number of turns from a configuration file
     * Expected format: "turns: <integer>"
     * @param filename Path to the configuration file
     * @return The maximum number of turns (1-8192)
     * @throws IOException If file cannot be read
     * @throws IllegalArgumentException If format is invalid or value is out of range
     */
    public static int readMaxTurns(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Parse "turns: <int>" format
                if (line.startsWith("turns:")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        try {
                            int turns = Integer.parseInt(parts[1].trim());

                            // Validate range [1-8192]
                            if (turns < 1 || turns > 8192) {
                                throw new IllegalArgumentException(
                                    "Turns value must be between 1 and 8192, got: " + turns
                                );
                            }

                            return turns;
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(
                                "Invalid turns value: " + parts[1].trim()
                            );
                        }
                    }
                }
            }

            throw new IllegalArgumentException(
                "Configuration file does not contain 'turns: <int>'"
            );
        }
    }
}
