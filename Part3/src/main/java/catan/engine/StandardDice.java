package catan.engine;

import catan.interfaces.IRandomDice;

import java.util.Random;

/**
 * Standard two six-sided dice implementation.
 * Implements IRandomDice for Dependency Inversion.
 */
public class StandardDice implements IRandomDice {
    private final Random random;

    public StandardDice() {
        this.random = new Random();
    }

    public StandardDice(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public int roll() {
        int die1 = random.nextInt(6) + 1;
        int die2 = random.nextInt(6) + 1;
        return die1 + die2;
    }
}
