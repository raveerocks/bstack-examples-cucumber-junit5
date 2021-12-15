package com.browserstack.steps;

import com.browserstack.ParallelPlatformTest;
import com.browserstack.util.Utility;
import com.browserstack.webdriver.config.Platform;
import com.browserstack.webdriver.core.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class SetupSteps {

    private final StepData stepData;
    private static WebDriverFactory webDriverFactory = WebDriverFactory.getInstance();

    public SetupSteps(StepData stepData) {
        this.stepData = stepData;
    }

    @Before
    public void setup(Scenario scenario){
        Platform platform;

        if(System.getProperty("parallel.feature","false").equals("true")){
            platform = webDriverFactory.getPlatforms().get(0);
        }
        else{
            platform = ParallelPlatformTest.threadLocalValue.get();
        }
        stepData.setUrl(webDriverFactory.getTestEndpoint());
        stepData.setWebDriver(webDriverFactory.createWebDriverForPlatform(platform,scenario.getName()));
    }

    @After
    public void tearDown(Scenario scenario){
        stepData.getWebDriver().quit();
        if (webDriverFactory.isCloudDriver()){
            Utility.setSessionStatus(stepData.getWebDriver(),"passed","Passed");
        }
    }
}
