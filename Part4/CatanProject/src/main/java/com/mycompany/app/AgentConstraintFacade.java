package com.mycompany.app;

/**
 * Façade for R3.3 constraint resolution. Stub implementation returns null
 * so agents fall back to R3.2 evaluation until full R3.3 is implemented.
 */
public class AgentConstraintFacade {

    public AgentConstraintFacade(CatanEngine engine) {
    }

    public ICommand getPriorityConstraintAction(Player agent) {
        return null;
    }
}
