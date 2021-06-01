package services;

import io.appium.java_client.android.AndroidDriver;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import services.utility.PropertyReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static services.utility.ReportService.catchException;

@Log4j
public class AndroidBaseTest {

    private AndroidDriver<?> driver;

    public AndroidBaseTest() {
            this.driver = setUp();
    }

    public AndroidDriver<?> getDriver() {
        return driver;
    }

    public static PropertyReader propertyReader = new PropertyReader("mobileApp.properties");
    protected String deviceName = propertyReader.getAnyVal("deviceName");
    public String platformName = propertyReader.getAnyVal("platformName");
    public String platformVersion = propertyReader.getAnyVal("platformVersion");
    public String udid = propertyReader.getAnyVal("udid");
    public String appPackage = propertyReader.getAnyVal("appPackage");
    public String appActivity = propertyReader.getAnyVal("appActivity");

    protected String port = propertyReader.getAnyVal("testserverport");
    public String server = propertyReader.getAnyVal("testserver");

    public AndroidDriver <?> setUp(){
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", deviceName);
        capabilities.setCapability("platformName", platformName);
        capabilities.setCapability("platformVersion", platformVersion);
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("appPackage", appPackage);
        capabilities.setCapability("appActivity", appActivity);

        AndroidDriver <?> driver = null;
        try {
            driver = new AndroidDriver(new URL(getGrid(server, port)), capabilities);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            driver.resetApp();
        } catch (MalformedURLException e) {
           catchException(e);
        }
        return driver;
    }

    public void quit(AndroidDriver <?> driver) {
        driver.quit();
    }

    private String getGrid(String gridValue, String port) {
        return "http://" + gridValue + ":" + port + "/wd/hub";
    }
}
