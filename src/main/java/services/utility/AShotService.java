package services.utility;

import lombok.extern.log4j.Log4j;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.StandardStream;
import org.openqa.selenium.*;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import services.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static services.Constants.IMAGE_DIFFERENCE_WITH_EXCLUDE;
import static services.utility.ReportService.catchException;

@Log4j
public class AShotService {

    public static boolean verifyImageLoaded(WebElement element, WebDriver driver){
        Boolean result = (Boolean) ((JavascriptExecutor)driver).
                executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", element);
        return result;
    }

    public static void storeImageByLink(WebElement element, String name){
        try {
            String link = element.getAttribute("src");
            URL url = new URL(link);
            BufferedImage img = ImageIO.read(url);
            ImageIO.write(img, "PNG", new File("target/screenshots/" + name + ".png"));
        }
        catch (IOException e){
            ReportService.assertTrue(false,"Catch "+e);
        }

    }

    public static boolean verifyAccuracy(int x1, int x2, int y1, int y2){
        int x = Math.abs(x1-x2);
        int y = Math.abs(y1-y2);
        if (x<= Constants.PIXEL_ACCURACY && y<=Constants.PIXEL_ACCURACY){
            return true;
        }
        else {
            log.error("More than accuracy. Width difference = "+ x + " Height difference = "+y);
            return false;
        }
    }

    public static double getDifference(File current, File expected){

        BufferedImage img1 = null;
        BufferedImage img2 = null;
        try {
            img1 = ImageIO.read(current);
            img2 = ImageIO.read(expected);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width1 = img1.getWidth(null);
        int width2 = img2.getWidth(null);
        int height1 = img1.getHeight(null);
        int height2 = img2.getHeight(null);
        if ((width1 != width2) || (height1 != height2)) {
            ReportService.assertTrue(verifyAccuracy(width1,width2,height1,height2),
                    "Different size.");
        }
        long diff = 0;
        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >>  8) & 0xff;
                int b1 = (rgb1      ) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >>  8) & 0xff;
                int b2 = (rgb2      ) & 0xff;
                diff += Math.abs(r1 - r2);
                diff += Math.abs(g1 - g2);
                diff += Math.abs(b1 - b2);
            }
        }
        double n = width1 * height1 * 3;
        double p = diff / n / 255.0;
        log.info("Difference percent: " + (p * 100.0));
        return p * 100.0;
    }

    public static boolean compareImages(File expected, File current, String diff ){
        CompareCmd cmd = new CompareCmd();

        cmd.setErrorConsumer(StandardStream.STDERR);
        IMOperation operation = new IMOperation();
        operation.metric("pae");

        String expectedPath = expected.getAbsolutePath();
        String currentPath = current.getAbsolutePath();

        operation.addImage(expectedPath);
        operation.addImage(currentPath);
        operation.addImage(diff);
        try {
            cmd.run(operation);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkImage(WebElement element, String name, WebDriver driver){
        if (verifyImageLoaded(element,driver)){
            storeImageByLink(element, name);
            File current = new File("target/screenshots/" + name + ".png");
            File expected = new File("src/test/resources/images/" + name + ".png");
            double diff = getDifference(current,expected);
            if (diff<= Constants.IMAGE_DIFFERENCE){
                return true;
            }
            else {
                log.info("Images are different.");
                compareImages(current,expected, "target/screenshots/diffOf" + name + ".png");
                return false;
            }
        }
        else {
            log.info("Image doesn't loaded.");
            return false;
        }
    }

    public static void takeScreenShot(String name, WebDriver driver){
        try {
            final Screenshot screenshot = new AShot()
                    .coordsProvider(new WebDriverCoordsProvider())
                    .takeScreenshot(driver);
            final BufferedImage image = screenshot.getImage();
            ImageIO.write(image, "PNG", new File("target/screenshots/"+name+".png"));
        }
        catch (IOException e){
            ReportService.assertTrue(false,"Catch "+e);
        }
    }

    public static void takeScreenShot(WebElement element, String name, WebDriver driver){
        try {
            final Screenshot screenshot = new AShot()
                    .coordsProvider(new WebDriverCoordsProvider())
                    //.coordsProvider(new CustomWebDriverCoordsProvider())
                    .takeScreenshot(driver, element);
            final BufferedImage image = screenshot.getImage();
            ImageIO.write(image, "PNG", new File("target/screenshots/"+name+".png"));
        }
        catch (IOException e){
            ReportService.assertTrue(false,"Catch "+e);
        }
    }

    /**
     * Screenhot with list elements
     * @param elements
     * @param name
     * @param driver
     */
    public static void takeScreenShot(List<WebElement> elements, String name, WebDriver driver){
        try {
            final Screenshot screenshot = new AShot()
                    .coordsProvider(new WebDriverCoordsProvider())
                    .takeScreenshot(driver, elements);
            final BufferedImage image = screenshot.getImage();
            ImageIO.write(image, "PNG", new File("target/screenshots/"+name+".png"));
        }
        catch (IOException e){
            ReportService.assertTrue(false,"Catch "+e);
        }
    }

    public static void takeScreenShot(WebElement element, String name, List<By> ignoredElements, WebDriver driver){
        try {
            //List to Set.
            Set<By> setIgnore = new HashSet<>(ignoredElements);

            final Screenshot screenshot = new AShot()
                    //.coordsProvider(new CustomWebDriverCoordsProvider()
                    .coordsProvider(new WebDriverCoordsProvider())
                    .ignoredElements(setIgnore)
                    .takeScreenshot(driver, element);
            final BufferedImage image = screenshot.getImage();
            ImageIO.write(image, "PNG", new File("target/screenshots/"+name+".png"));
        }
        catch (IOException e){
            ReportService.assertTrue(false,"Catch "+e);
        }
    }

    public static File getScreenShot(WebElement element, String name, WebDriver driver){

        for (int i = 0; i <5 ; i++) {
            try {
                File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                final BufferedImage image = ImageIO.read(scrFile);
                Point point = element.getLocation();
                int width = element.getSize().getWidth();
                int height = element.getSize().getHeight();
                BufferedImage elementScreen = image.getSubimage(point.getX(),point.getY(), width, height);
                ImageIO.write(elementScreen, "png", new File("target/screenshots/"+name+".png"));
                return scrFile;
            }
            catch (IOException e){
                ReportService.assertTrue(false, "Catch "+e);
                return null;
            }
            catch (RasterFormatException e){
                ReportService.assertTrue(false, "Catch "+e);
            }
        }
        return null;
    }

    public static boolean checkElement(WebElement element, String name, WebDriver driver){

        takeScreenShot(element, name, driver);

        File current = new File("target/screenshots/" + name + ".png");
        File expected = new File("src/test/resources/images/" + name + ".png");
        double diff = getDifference(current,expected);
        if (diff <= Constants.IMAGE_DIFFERENCE){
            return true;
        }
        else {
            log.error("Images are different.");
            compareImages(current,expected, "target/screenshots/diffOf" + name + ".png");
            return false;
        }
    }

    public static boolean checkElement(WebElement element, String name, List<By> ignoredElements, WebDriver driver){

        if(ignoredElements.isEmpty())
            takeScreenShot(element, name, driver);
        else{
            takeScreenShot(element, name, ignoredElements, driver);
        }

        File current = new File("target/screenshots/" + name + ".png");
        File expected = new File("src/test/resources/images/" + name + ".png");
        double diff = getDifference(current,expected);
        if (diff<= Constants.IMAGE_DIFFERENCE){
            return true;
        }
        else {
            log.info("Images are different.");
            compareImages(current,expected, "target/screenshots/diffOf" + name + ".png");
            return false;
        }
    }

    /**
     * @param name of screenshot
     * @param ignoredElements list element that will not be screenshoted
     * @param driver
     * @return Screenshot object for compare
     */
    public static Screenshot takeScreenShot(String name, List<By> ignoredElements, WebDriver driver){

        //List to Set.
        Set<By> setIgnore = new HashSet<>(ignoredElements);
        Screenshot screenshot = null;
        try {
             screenshot = new AShot()
                    .ignoredElements(setIgnore)
                    .coordsProvider(new WebDriverCoordsProvider())
                    .takeScreenshot(driver);
            final BufferedImage image = screenshot.getImage();
            ImageIO.write(image, "PNG", new File("target/screenshots/"+name+".png"));
        }
        catch (IOException e){
            ReportService.assertTrue(false,"Catch "+e);
        }
        return screenshot;
    }


    /**
     * @param ignoredElements list element that will not be screenshoted
     * @param name of screenshot
     * @param driver
     * @return bool
     */
    public static boolean checkElement(List<By> ignoredElements, String name, WebDriver driver){

        takeScreenShot(name, ignoredElements, driver);

        //Current.
        Screenshot currentScreenshot = takeScreenShot(name, ignoredElements, driver);

        //Expected.
        Screenshot expectedScreenshot = getActualScreenshots(currentScreenshot, name, "src/test/resources/images/");

        ImageDiff diff = getDiffSize(expectedScreenshot,currentScreenshot);
        log.info("Diff size - " + diff.getDiffSize());
        if (diff.getDiffSize()<= IMAGE_DIFFERENCE_WITH_EXCLUDE){
            return true;
        }

        else {
            log.error("Images are different.");
            File diffFile = new File("target/screenshots/diffOf_" + name + ".png");
            try {
                ImageIO.write(diff.getMarkedImage(), "png", diffFile);
            }
            catch (IOException e) {
                catchException(e);
            }
            return false;
        }

    }

    /**
     *
     * @param expected screenshot from /images/ (reference picture)
     * @param current  actual screenshot
     * @return object ImageDiff
     */
    public static ImageDiff getDiffSize(Screenshot expected, Screenshot current){
        return new ImageDiffer().makeDiff(expected, current);
    }

    /**
     *
     * @param currentScreenshot with ignored areas
     * @param name of screenshot
     * @return  actual screenshot
     */
    public static Screenshot getActualScreenshots(Screenshot currentScreenshot, String name, String pathToFile){
        Screenshot expectedScreenshot = null;
        try {
            expectedScreenshot = new Screenshot(ImageIO.read(new File(pathToFile + name + ".png")));
            expectedScreenshot.setIgnoredAreas(currentScreenshot.getIgnoredAreas());
        }
        catch (IOException e) {
            catchException(e);
        }
        return expectedScreenshot;
    }

    /**
     * @param ignoredElements list element that will not be screenshoted
     * @param name of screenshot (from google drive)
     * @param driver
     * @return bool
     */
    public static boolean checkElementFromDrive(List<By> ignoredElements, String name, WebDriver driver){

        takeScreenShot(name, ignoredElements, driver);

        //Current.
        Screenshot currentScreenshot = takeScreenShot(name, ignoredElements, driver);

        //Expected.
        Screenshot expectedScreenshot = getActualScreenshots(currentScreenshot, name, "target/screenshotsExpected/");

        ImageDiff diff = getDiffSize(expectedScreenshot,currentScreenshot);
        log.info("Diff size - " + diff.getDiffSize());
        if (diff.getDiffSize()<= IMAGE_DIFFERENCE_WITH_EXCLUDE){
            return true;
        }

        else {
            log.info("Images are different.");
            File diffFile = new File("target/screenshots/diffOf_" + name + ".png");
            try {
                ImageIO.write(diff.getMarkedImage(), "png", diffFile);
            }
            catch (IOException e) {
                catchException(e);
            }
            return false;
        }

    }

    public static boolean checkElementFromDrive(WebElement element, String name, List<By> ignoredElements, WebDriver driver){

        if(ignoredElements.isEmpty())
            takeScreenShot(element, name, driver);
        else{
            takeScreenShot(element, name, ignoredElements, driver);
        }

        File current = new File("target/screenshots/" + name + ".png");
        File expected = new File("target/screenshotsExpected/" + name + ".png");
        double diff = getDifference(current,expected);
        if (diff<= Constants.IMAGE_DIFFERENCE){
            return true;
        }
        else {
            log.info("Images are different.");
            compareImages(current,expected, "target/screenshots/diffOf" + name + ".png");
            return false;
        }
    }
}
