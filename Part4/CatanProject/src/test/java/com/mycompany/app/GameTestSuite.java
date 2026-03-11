package com.mycompany.app;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AppTest.class,
        PlayerTest.class,
        BoardTest.class,
        CatanEngineTest.class,
        SimulatorTest.class,
        RoadValidatorTest.class,
        ResourceDistributorTest.class,
        RandomAgentTest.class,
        BuildingCostTest.class
})
public class GameTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
