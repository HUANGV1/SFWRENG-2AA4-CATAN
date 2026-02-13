package catan.interfaces;

/**
 * Abstraction for dice rolling (Dependency Inversion Principle).
 * Allows injection of deterministic dice for testing.
 */
public interface IRandomDice {
    /**
     * Roll the dice and return the sum.
     * 
     * @return an integer representing the dice result (typically 2-12)
     */
    int roll();
}
