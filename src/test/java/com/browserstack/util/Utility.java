package com.browserstack.util;


import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Utility {

    private Utility() {
    }


    private static final String LOCATION_SCRIPT_FORMAT = "navigator.geolocation.getCurrentPosition = function(success){\n" +
            "    var position = { \"coords\":{\"latitude\":\"%s\",\"longitude\":\"%s\"}};\n" +
            "    success(position);\n" +
            "}";
    private static final String OFFER_LATITUDE = "19";
    private static final String OFFER_LONGITUDE = "72";



    public static void setSessionStatus(WebDriver webDriver, String status, String reason) {
        JavascriptExecutor jse = (JavascriptExecutor) webDriver;
        jse.executeScript(String.format("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"%s\", \"reason\": \"%s\"}}", status, reason));
    }


    public static boolean isAscendingOrder(List<WebElement> priceWebElement, int length) {
        if (priceWebElement == null || length < 2)
            return true;
        if (Integer.parseInt(priceWebElement.get(length - 2).getText()) > Integer.parseInt(priceWebElement.get(length - 1).getText()))
            return false;
        return isAscendingOrder(priceWebElement, length - 1);
    }

  /*  public static void setLocationSpecificCapabilities(DesiredCapabilities desiredCapabilities) {
        String browser = (String) desiredCapabilities.getCapability("browser");
        if (browser != null) {
            if (browser.equalsIgnoreCase("Chrome")) {
                desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, getChromeOptions());
            } else if (browser.equalsIgnoreCase("Firefox")) {
                desiredCapabilities.setCapability(FirefoxDriver.PROFILE, getFirefoxProfile());
            }
        }
    }*/

    public static void mockGPS(WebDriver webDriver) {
        String locationScript = String.format(LOCATION_SCRIPT_FORMAT, OFFER_LATITUDE, OFFER_LONGITUDE);
        ((JavascriptExecutor) webDriver).executeScript(locationScript);
    }

  /*  private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        Map<String, Object> profile = new HashMap<>();
        Map<String, Object> contentSettings = new HashMap<>();
        contentSettings.put("geolocation", 1);
        profile.put("managed_default_content_settings", contentSettings);
        prefs.put("profile", profile);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    private static FirefoxProfile getFirefoxProfile() {
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference("geo.enabled", false);
        firefoxProfile.setPreference("geo.provider.use_corelocation", false);
        firefoxProfile.setPreference("geo.prompt.testing", false);
        firefoxProfile.setPreference("geo.prompt.testing.allow", false);
        return firefoxProfile;
    }*/

}
