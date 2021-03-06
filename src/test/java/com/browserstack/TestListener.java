package com.browserstack;


import com.browserstack.webdriver.core.WebDriverFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseFinished;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class TestListener implements ConcurrentEventListener {

    public static ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebDriverFactory webDriverFactory = WebDriverFactory.getInstance();

    public static WebDriver getWebDriver() {
        WebDriver webDriver = webDriverThreadLocal.get();
        webDriverThreadLocal.remove();
        return webDriver;
    }

    public static void setWebDriver(WebDriver webDriver) {
        TestListener.webDriverThreadLocal.set(webDriver);
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::markAndCloseWebDriver);
    }

    public void markAndCloseWebDriver(TestCaseFinished testCaseFinished) {
        WebDriver webDriver = getWebDriver();
        try {
            if (webDriverFactory.isCloudDriver()) {
                String status = "passed";
                String reason = "Test Passed";
                if (!testCaseFinished.getResult().getStatus().isOk()) {
                    status = "failed";
                    reason = testCaseFinished.getResult().getError().toString();
                }
                String script = createExecutorScript(status, reason);
                if (StringUtils.isNotEmpty(script)) {
                    ((JavascriptExecutor) webDriver).executeScript(script);
                }
            }
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }

    private String createExecutorScript(String status, String reason) {
        ObjectNode rootNode = objectMapper.createObjectNode();
        ObjectNode argumentsNode = objectMapper.createObjectNode();

        // Read only the first line of the error message
        reason = reason.split("\n")[0];
        // Limit the error message to only 255 characters
        if (reason.length() >= 255) {
            reason = reason.substring(0, 255);
        }
        // Replacing all the special characters with whitespace
        reason.replaceAll("^[^a-zA-Z0-9]", " ");

        argumentsNode.put("status", status);
        argumentsNode.put("reason", reason);

        rootNode.put("action", "setSessionStatus");
        rootNode.set("arguments", argumentsNode);
        String executorStr = "";
        try {
            executorStr = objectMapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            throw new Error("Error creating JSON object for Marking tests", e);
        }
        return "browserstack_executor: " + executorStr;
    }
}
