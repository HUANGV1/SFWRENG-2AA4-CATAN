package com.mycompany.app;

import java.util.Random;

/**
 * Standard implementation of IRandomDice that simulates rolling two six-sided dice.
 * Returns values between 2 and 12 (inclusive).
 */
public class StandardDice implements IRandomDice {
    private Random random;

    /**
     * Constructor for StandardDice
     */
    public StandardDice() {
        this.random = new Random();
    }

    /**
     * Rolls two six-sided dice and returns the sum
     * @return The sum of two dice rolls (2-12)
     */
    @Override
    public int roll() {
        // Roll two six-sided dice
        int die1 = random.nextInt(6) + 1;  // 1-6
        int die2 = random.nextInt(6) + 1;  // 1-6
        return die1 + die2;  // 2-12
    }
}
