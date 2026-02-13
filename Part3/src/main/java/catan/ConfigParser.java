package catan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads configuration parameters from a file.
 */
public class ConfigParser {

    /**
     * Read the maximum number of turns from a config file.
     * The file should contain a single integer on the first line.
     *
     * @param filename path to the config file
     * @return the max turns value, or a default of 8192 if file cannot be read
     */
    public static int readMaxTurns(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Could not read config file: " + filename
                    + " - using default max turns (8192)");
        }
        return 8192;
    }
}
