package catan.player;

import catan.enums.BuildingCost;
import catan.enums.ResourceType;
import catan.interfaces.IGameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A random agent that selects actions uniformly at random.
 * On each turn, it enumerates all legal AND affordable actions,
 * picks one at random, and repeats until no actions remain.
 */
public class RandomAgent extends Player {
    private final Random random;
    private int lastPlacedSettlement;

    public RandomAgent(int playerID) {
        super(playerID);
        this.random = new Random();
        this.lastPlacedSettlement = -1;
    }

    public RandomAgent(int playerID, long seed) {
        super(playerID);
        this.random = new Random(seed);
        this.lastPlacedSettlement = -1;
    }

    /** Lightweight action descriptor */
    private enum ActionType {
        BUILD_SETTLEMENT, BUILD_ROAD
    }

    private record GameAction(ActionType type, int location) {
    }

    @Override
    public void takeTurn(IGameController controller) {
        while (true) {
            List<GameAction> legalActions = enumerateActions(controller);
            if (legalActions.isEmpty()) {
                break;
            }
            GameAction chosen = legalActions.get(random.nextInt(legalActions.size()));
            boolean success = executeAction(controller, chosen);
            if (!success) {
                // Action failed (shouldn't happen given affordability check, but safety net)
                break;
            }
        }
    }

    private List<GameAction> enumerateActions(IGameController controller) {
        List<GameAction> actions = new ArrayList<>();

        // Check valid settlement locations (only if affordable)
        if (hasResources(BuildingCost.SETTLEMENT.getCost())) {
            List<Integer> settlementLocations = controller.getValidSettlementLocations(playerID);
            for (int nodeId : settlementLocations) {
                actions.add(new GameAction(ActionType.BUILD_SETTLEMENT, nodeId));
            }
        }

        // Check valid road locations (only if affordable)
        if (hasResources(BuildingCost.ROAD.getCost())) {
            List<Integer> roadLocations = controller.getValidRoadLocations(playerID);
            for (int edgeId : roadLocations) {
                actions.add(new GameAction(ActionType.BUILD_ROAD, edgeId));
            }
        }

        return actions;
    }

    private boolean executeAction(IGameController controller, GameAction action) {
        return switch (action.type()) {
            case BUILD_SETTLEMENT -> controller.requestBuildSettlement(playerID, action.location());
            case BUILD_ROAD -> controller.requestBuildRoad(playerID, action.location());
        };
    }

    /**
     * Place an initial settlement at a random valid location.
     */
    public void placeInitialSettlement(IGameController controller, boolean isSetupPhase) {
        List<Integer> validLocations = controller.getValidSettlementLocations(playerID);
        if (!validLocations.isEmpty()) {
            int location = validLocations.get(random.nextInt(validLocations.size()));
            controller.requestBuildSettlement(playerID, location);
            this.lastPlacedSettlement = location;
        }
    }

    /**
     * Place an initial road adjacent to the last settlement.
     */
    public void placeInitialRoad(IGameController controller) {
        List<Integer> validLocations = controller.getValidRoadLocations(playerID);
        if (!validLocations.isEmpty()) {
            int location = validLocations.get(random.nextInt(validLocations.size()));
            controller.requestBuildRoad(playerID, location);
        }
    }

    public int getLastPlacedSettlement() {
        return lastPlacedSettlement;
    }

    @Override
    public void handleOverSevenCards() {
        int totalCards = getTotalResourceCount();
        if (totalCards > 7) {
            int cardsToDiscard = totalCards / 2;
            List<ResourceType> availableResources = new ArrayList<>();

            for (ResourceType type : ResourceType.values()) {
                for (int i = 0; i < resources.get(type); i++) {
                    availableResources.add(type);
                }
            }

            for (int i = 0; i < cardsToDiscard && !availableResources.isEmpty(); i++) {
                int index = random.nextInt(availableResources.size());
                ResourceType typeToRemove = availableResources.remove(index);
                removeResource(typeToRemove, 1);
            }
        }
    }
}
