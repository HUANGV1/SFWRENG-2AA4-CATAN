package com.mycompany.app;

import com.mycompany.app.commands.BuildCityCommand;
import com.mycompany.app.commands.BuildRoadCommand;
import com.mycompany.app.commands.BuildSettlementCommand;
import com.mycompany.app.commands.BuyDevCardCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rule-based agent: R3.3 constraints via AgentConstraintFacade, then R3.2
 * scoring via Template Method (evaluateCommand).
 */
public class RuleBasedAgent extends ComputerPlayer {

    private final AgentConstraintFacade facade;

    public RuleBasedAgent(int playerID, CatanEngine engine) {
        super(playerID);
        this.facade = new AgentConstraintFacade(engine);
    }

    @Override
    public void takeTurn(IGameController controller) {
        final int maxBuildsPerTurn = 10;
        int buildsThisTurn = 0;

        while (buildsThisTurn < maxBuildsPerTurn) {
            ICommand priority = facade.getPriorityConstraintAction(this);
            if (priority != null) {
                priority.execute(controller, this);
                buildsThisTurn++;
                continue;
            }

            List<ICommand> candidates = buildCandidateList(controller);
            ICommand best = chooseBestAction(candidates);
            if (best == null) {
                return;
            }
            best.execute(controller, this);
            buildsThisTurn++;
        }
    }

    @Override
    protected double evaluateCommand(ICommand command) {
        if (command instanceof BuildSettlementCommand || command instanceof BuildCityCommand) {
            return 1.0;
        }
        if (command instanceof BuildRoadCommand || command instanceof BuyDevCardCommand) {
            return 0.8;
        }
        if (estimateCardsAfterSpend(command) < 5) {
            return 0.5;
        }
        return 0.0;
    }

    private int estimateCardsAfterSpend(ICommand command) {
        int current = getTotalResourceCards();
        Map<ResourceType, Integer> cost = null;
        if (command instanceof BuildSettlementCommand) {
            cost = BuildingCost.SETTLEMENT.getCost();
        } else if (command instanceof BuildCityCommand) {
            cost = BuildingCost.CITY.getCost();
        } else if (command instanceof BuildRoadCommand) {
            cost = BuildingCost.ROAD.getCost();
        }
        if (cost == null) {
            return current;
        }
        int totalCost = cost.values().stream().mapToInt(Integer::intValue).sum();
        return current - totalCost;
    }

    @Override
    public void handleOverSevenCards() {
    }

    @Override
    public void robberDiscard(int amountToDrop) {
        int remaining = amountToDrop;
        while (remaining > 0 && getTotalResourceCards() > 0) {
            List<ResourceType> available = new ArrayList<>();
            for (ResourceType type : ResourceType.values()) {
                if (getResourceCount(type) > 0) {
                    available.add(type);
                }
            }
            if (available.isEmpty()) {
                break;
            }
            ResourceType chosen = available.get(random.nextInt(available.size()));
            deductResource(chosen, 1);
            remaining--;
        }
    }

    @Override
    public ResourceType stealRandomResource() {
        List<ResourceType> available = new ArrayList<>();
        for (ResourceType type : ResourceType.values()) {
            if (getResourceCount(type) > 0) {
                available.add(type);
            }
        }
        if (available.isEmpty()) {
            return null;
        }
        ResourceType chosen = available.get(random.nextInt(available.size()));
        deductResource(chosen, 1);
        return chosen;
    }
}
