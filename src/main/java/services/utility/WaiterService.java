package services.utility;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.testng.Assert.fail;
import static services.Constants.PAGE_TIMEOUT;

@Log4j
public class WaiterService {

    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitForJSandJQueryToLoad(WebDriver driver) {
        ExpectedCondition<Boolean> jQueryLoad = driver1 -> {
            try {
                return ((Long) ((JavascriptExecutor) driver).executeScript("return !!window.jQuery && jQuery.active") == 0);
            } catch (Exception e) {
                return true;
            }
        };

        ExpectedCondition<Boolean> jsLoad = driver2 -> ((JavascriptExecutor) driver).executeScript("return document.readyState")
                .toString().equals("complete");

        try {
            WebDriverWait wait = new WebDriverWait(driver, PAGE_TIMEOUT);
            wait.until(jQueryLoad);
            wait.until(jsLoad);
        } catch (TimeoutException e) {
            log.info("No one jQuery or Js activity");
        }
    }

    public static void waitForElementVisible(AndroidElement element, AndroidDriver<?> driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 40);
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            fail("ELEMENT: \"" + element + "\" is not presents");
        } catch (StaleElementReferenceException e) {
            log.warn("ELEMENT: \"" + element + "\" is not found in the cache.");
        }

    }

}
