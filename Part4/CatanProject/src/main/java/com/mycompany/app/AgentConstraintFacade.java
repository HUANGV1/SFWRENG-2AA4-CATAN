package com.mycompany.app;

import com.mycompany.app.commands.BuildCityCommand;
import com.mycompany.app.commands.BuildRoadCommand;
import com.mycompany.app.commands.BuildSettlementCommand;

import java.util.*;

/**
 * Façade for R3.3 constraint resolution. Checks constraints in priority order:
 * A) Over-7 cards: spend via road/settlement/city (cheapest first)
 * B) Two road segments within 2 edge-units: bridge with BuildRoadCommand
 * C) Longest-road competition: extend road to maximize predicted length
 */
public class AgentConstraintFacade {

    private final CatanEngine engine;
    private final IBoardGraph topology;

    public AgentConstraintFacade(CatanEngine engine) {
        this.engine = engine;
        this.topology = engine.getBoard().getTopology();
    }

    public ICommand getPriorityConstraintAction(Player agent) {
        ICommand cmd = constraintOverSevenCards(agent);
        if (cmd != null) return cmd;
        cmd = constraintConnectRoadsWithinTwo(agent);
        if (cmd != null) return cmd;
        cmd = constraintLongestRoadCompetition(agent);
        if (cmd != null) return cmd;
        return null;
    }

    private ICommand constraintOverSevenCards(Player agent) {
        if (agent.getTotalResourceCards() <= 7) {
            return null;
        }
        int agentId = agent.getPlayerID();

        if (agent.hasResources(BuildingCost.ROAD.getCost())) {
            int[] roadLocs = engine.getValidRoadLocations(agentId);
            if (roadLocs != null && roadLocs.length > 0) {
                return new BuildRoadCommand(roadLocs[0]);
            }
        }
        if (agent.hasResources(BuildingCost.SETTLEMENT.getCost())) {
            int[] settlementLocs = engine.getValidSettlementLocations(agentId);
            if (settlementLocs != null && settlementLocs.length > 0) {
                return new BuildSettlementCommand(settlementLocs[0]);
            }
        }
        if (agent.hasResources(BuildingCost.CITY.getCost())) {
            for (Node node : engine.getBoard().getAllNodes()) {
                if (node.getOccupant() != null
                        && node.getOccupant().getPlayerID() == agentId
                        && node.getType() == BuildingType.SETTLEMENT) {
                    return new BuildCityCommand(node.getNodeID());
                }
            }
        }
        return null;
    }

    private ICommand constraintConnectRoadsWithinTwo(Player agent) {
        int agentId = agent.getPlayerID();
        Set<Integer> ownedEdges = new HashSet<>();
        for (Edge e : engine.getBoard().getAllEdges()) {
            if (e.hasRoadBy(agentId)) {
                ownedEdges.add(e.getEdgeID());
            }
        }
        if (ownedEdges.size() < 2) {
            return null;
        }

        Set<Integer> validBuildEdges = new HashSet<>();
        for (int eid : engine.getValidRoadLocations(agentId)) {
            validBuildEdges.add(eid);
        }

        Board board = engine.getBoard();
        for (int e1 : ownedEdges) {
            Map<Integer, Integer> dist = new HashMap<>();
            Map<Integer, Integer> parent = new HashMap<>();
            dist.put(e1, 0);
            Queue<Integer> q = new ArrayDeque<>();
            q.add(e1);
            while (!q.isEmpty()) {
                int cur = q.poll();
                int d = dist.get(cur);
                if (d >= 2) continue;
                for (int next : neighborEdges(cur)) {
                    if (dist.containsKey(next)) continue;
                    dist.put(next, d + 1);
                    parent.put(next, cur);
                    q.add(next);
                    if (d + 1 == 2 && ownedEdges.contains(next)) {
                        int bridge = parent.get(next);
                        Edge be = board.getEdge(bridge);
                        if (be != null && !be.hasRoad() && validBuildEdges.contains(bridge)
                                && agent.hasResources(BuildingCost.ROAD.getCost())) {
                            return new BuildRoadCommand(bridge);
                        }
                    }
                }
            }
        }
        return null;
    }

    private Set<Integer> neighborEdges(int edgeId) {
        Set<Integer> out = new HashSet<>();
        int[] endpoints = topology.getEdgeEndpoints(edgeId);
        if (endpoints == null) return out;
        for (int nodeId : endpoints) {
            int[] adj = topology.getAdjacentEdges(nodeId);
            if (adj != null) {
                for (int e : adj) {
                    out.add(e);
                }
            }
        }
        return out;
    }

    private ICommand constraintLongestRoadCompetition(Player agent) {
        int agentId = agent.getPlayerID();
        Board board = engine.getBoard();
        int L_agent = RoadGraphMetrics.longestRoadLength(board, topology, agentId);

        for (Player p : engine.getPlayers()) {
            if (p.getPlayerID() == agentId) continue;
            int L_other = RoadGraphMetrics.longestRoadLength(board, topology, p.getPlayerID());
            if (L_other >= L_agent - 1) {
                if (!agent.hasResources(BuildingCost.ROAD.getCost())) {
                    return null;
                }
                int[] validLocs = engine.getValidRoadLocations(agentId);
                if (validLocs == null || validLocs.length == 0) {
                    return null;
                }
                int bestEdge = validLocs[0];
                int bestLen = RoadGraphMetrics.longestRoadLength(board, topology, agentId, bestEdge);
                for (int i = 1; i < validLocs.length; i++) {
                    int c = validLocs[i];
                    int len = RoadGraphMetrics.longestRoadLength(board, topology, agentId, c);
                    if (len > bestLen) {
                        bestLen = len;
                        bestEdge = c;
                    }
                }
                return new BuildRoadCommand(bestEdge);
            }
        }
        return null;
    }
}
