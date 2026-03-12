package com.mycompany.app;

/**
 * Observer interface for the Observer pattern.
 */
public interface IObserver {
    /**
     * Called when the observed subject's state changes.
     */
    public void update();
}
