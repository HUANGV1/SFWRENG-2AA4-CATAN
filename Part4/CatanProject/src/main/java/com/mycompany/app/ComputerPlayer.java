package com.mycompany.app;

import com.mycompany.app.commands.BuildCityCommand;
import com.mycompany.app.commands.BuildRoadCommand;
import com.mycompany.app.commands.BuildSettlementCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for computer-controlled players using the Template Method pattern.
 * Defines the action-selection algorithm; subclasses provide scoring.
 */
public abstract class ComputerPlayer extends Player {

    protected final Random random = new Random();

    public ComputerPlayer(int playerID) {
        super(playerID);
    }

    /**
     * Template method: selects the best command from the list using subclass scoring.
     */
    public final ICommand chooseBestAction(List<ICommand> validCommands) {
        if (validCommands == null || validCommands.isEmpty()) {
            return null;
        }
        double maxScore = -1.0;
        List<ICommand> bestCommands = new ArrayList<>();

        for (ICommand command : validCommands) {
            double score = evaluateCommand(command);
            if (score > maxScore) {
                bestCommands.clear();
                bestCommands.add(command);
                maxScore = score;
            } else if (score == maxScore) {
                bestCommands.add(command);
            }
        }

        if (bestCommands.isEmpty()) {
            return null;
        }
        return bestCommands.get(random.nextInt(bestCommands.size()));
    }

    /**
     * Primitive operation: subclasses implement scoring logic.
     */
    protected abstract double evaluateCommand(ICommand command);

    /**
     * Builds the list of all legal, affordable commands for the current turn.
     */
    protected List<ICommand> buildCandidateList(IGameController controller) {
        List<ICommand> list = new ArrayList<>();

        if (controller instanceof CatanEngine) {
            CatanEngine eng = (CatanEngine) controller;
            for (Node node : eng.getBoard().getAllNodes()) {
                if (node.getOccupant() != null
                        && node.getOccupant().getPlayerID() == playerID
                        && node.getType() == BuildingType.SETTLEMENT
                        && hasResources(BuildingCost.CITY.getCost())) {
                    list.add(new BuildCityCommand(node.getNodeID()));
                }
            }
        }

        if (hasResources(BuildingCost.SETTLEMENT.getCost())) {
            for (int nodeID : controller.getValidSettlementLocations(playerID)) {
                list.add(new BuildSettlementCommand(nodeID));
            }
        }

        if (hasResources(BuildingCost.ROAD.getCost())) {
            for (int edgeID : controller.getValidRoadLocations(playerID)) {
                list.add(new BuildRoadCommand(edgeID));
            }
        }

        return list;
    }
}
