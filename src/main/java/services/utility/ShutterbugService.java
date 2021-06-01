package services.utility;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static services.utility.ReportService.catchException;

@Log4j
public class ShutterbugService {

    public static void takeScreenshot(WebDriver driver){
        Shutterbug.shootPage(driver)
                .save("target/screenshots/");
    }
    public static void takeScreenshot(WebDriver driver, boolean value,String button){
        Shutterbug.shootPage(driver)
                .save("target/screenshots/forAndrei/button"+button);
    }

    public static void takeScreenshot(WebElement element, WebDriver driver){
        Shutterbug.shootElement(driver, element)
                .save("target/screenshots/");
    }
    public static void takeScreenshot(WebElement element, WebDriver driver, boolean value){
        Shutterbug.shootElement(driver, element)
                .save("target/screenshots/forAndrei");
    }


    public static boolean takeScreenshotAndCompareDiff(WebElement element, WebDriver driver, String image){
        boolean equals = false;
        try {
            equals = Shutterbug.shootElement(driver, element)
                    .equalsWithDiff("src/test/resources/images/"+ image + ".png",
                            "target/screenshots/diffOf" + image,  0.001);
        } catch (IOException e) {
            catchException(e);
        }
        if(!equals){
            log.info("Incorrect image, take new image");
            takeScreenshot(element, driver);
        }
        return equals;
    }

    public static boolean takeScreenshotAndCompareDiff(WebDriver driver, String image){
        boolean equals = false;
        try {
            equals = Shutterbug.shootPage(driver)
                    .equalsWithDiff("src/test/resources/images/"+ image + ".png",
                            "target/screenshots/diffOf" + image + ".png",0.001);
        } catch (IOException e) {
            catchException(e);
        }
        if(!equals){
            log.info("Incorrect image, take new image");
            takeScreenshot(driver);
        }
        return equals;
    }
}
