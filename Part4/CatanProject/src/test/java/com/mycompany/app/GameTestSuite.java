package com.mycompany.app;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        HumanInputParserTest.class,
        AppTest.class,
        PlayerTest.class,
        BoardTest.class,
        BuildingCostTest.class,
        ResourceDistributorTest.class,
        CatanEngineTest.class,
        RoadValidatorTest.class,
        RandomAgentTest.class,
        SimulatorTest.class
})
public class GameTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
