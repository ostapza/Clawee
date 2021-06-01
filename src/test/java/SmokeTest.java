import io.appium.java_client.android.AndroidDriver;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;
import pageobjects.mobile.MobilePage;
import services.AndroidBaseTest;
import services.BaseTest;
import services.utility.WaiterService;

import java.util.NoSuchElementException;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


@Log4j
public class SmokeTest extends BaseTest implements HelpMethods {

    @Test(description = "Test - Guest start")
    public void test_001() throws InterruptedException {

        log.info("Run mobile application");
        AndroidBaseTest androidBaseTest = new AndroidBaseTest();
        AndroidDriver<?> driver = androidBaseTest.getDriver();
        try {
            log.info("Log in at google acc");
            MobilePage mobilePage = new MobilePage(driver);
            WaiterService.waitForElementVisible(mobilePage.googleButton, driver);
            Assert.assertTrue(mobilePage.googleButton.isDisplayed(), "No Google sigh-in button");
            mobilePage.googleButton.click();
            WaiterService.waitForElementVisible(mobilePage.googleAcc, driver);
            Assert.assertTrue(mobilePage.googleAcc.isDisplayed(), "No already logged account");
            mobilePage.googleAcc.click();

            log.info("click next&ok buttons on the popup");
             WaiterService.waitForElementVisible(mobilePage.sliderPopup, driver);
             while (mobilePage.sliderPopup.isDisplayed()) {
                 WaiterService.waitForElementVisible(mobilePage.nextButton, driver);
                 if(mobilePage.lastSlideText.getText().equals("You received a Welcome Bonus: 0 Coins")){
                     log.info("Close popup");
                     mobilePage.nextButton.click();
                     break;
                 }
                 mobilePage.nextButton.click();
             }


            log.info("Close daily popup");
            WaiterService.waitForElementVisible(mobilePage.nextButton, driver);
            mobilePage.nextButton.click();

            log.info("Go back to games");
            WaiterService.waitForElementVisible(mobilePage.backButton, driver);
            mobilePage.backButton.click();



            log.info("Close banner and assert that we go to the game menu");
            WaiterService.waitForElementVisible(mobilePage.nextButton, driver);
            mobilePage.nextButton.click();
            WaiterService.waitForElementVisible(mobilePage.gameMenu, driver);
            mobilePage.fistGame.click();

            log.info("press start button");
            WaiterService.waitForElementVisible(mobilePage.startButton, driver);
            mobilePage.startButton.click();

        } finally {
            androidBaseTest.quit(driver);
        }

    }
}




