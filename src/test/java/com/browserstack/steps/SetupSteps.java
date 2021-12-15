package com.browserstack.steps;

import com.browserstack.ParallelPlatformTest;
import com.browserstack.TestListener;
import com.browserstack.webdriver.config.Platform;
import com.browserstack.webdriver.core.WebDriverFactory;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;


public class SetupSteps {

    private static WebDriverFactory webDriverFactory = WebDriverFactory.getInstance();
    private final StepData stepData;

    public SetupSteps(StepData stepData) {
        this.stepData = stepData;
    }

    @Before
    public void setup(Scenario scenario) {
        Platform platform;

        if (System.getProperty("parallel.feature", "false").equals("true")) {
            platform = webDriverFactory.getPlatforms().get(0);
        } else {
            platform = ParallelPlatformTest.threadLocalValue.get();
        }
        stepData.setUrl(webDriverFactory.getTestEndpoint());
        WebDriver webDriver = webDriverFactory.createWebDriverForPlatform(platform, scenario.getName());
        stepData.setWebDriver(webDriver);
        TestListener.setWebDriver(webDriver);
    }
}
