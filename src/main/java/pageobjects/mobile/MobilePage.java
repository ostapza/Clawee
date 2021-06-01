package pageobjects.mobile;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.AndroidTouchAction;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.offset.PointOption;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.support.PageFactory;

@Log4j
@Data
@NoArgsConstructor
public class MobilePage {

    private AndroidDriver<?> driver;

    public MobilePage(AndroidDriver<?> driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    @AndroidFindBy(id = "com.gigantic.clawee:id/googleButton")
    public AndroidElement googleButton;

    @AndroidFindBy(xpath = "//android.widget.LinearLayout[1]/android.widget.LinearLayout")
    public AndroidElement googleAcc;

    @AndroidFindBy(id = "com.gigantic.clawee:id/guestButton")
    public AndroidElement guest;


    @AndroidFindBy(xpath = "//android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout")
    public AndroidElement sliderPopup;

    @AndroidFindBy(xpath = "//android.widget.ImageView[2]")
    public AndroidElement nextButton;

    @AndroidFindBy(xpath = "//android.widget.TextView")
    public AndroidElement lastSlideText;

    @AndroidFindBy(id = "com.gigantic.clawee:id/backImage")
    public AndroidElement backButton;

    @AndroidFindBy(id = "com.gigantic.clawee:id/titleText")
    public AndroidElement dailyPopup;

    @AndroidFindBy(xpath = "//android.view.ViewGroup[1]/androidx.cardview.widget.CardView/android.view.ViewGroup")
    public AndroidElement fistGame;

    @AndroidFindBy(id = "com.gigantic.clawee:id/pagerContainer")
    public AndroidElement gameMenu;

    @AndroidFindBy(id = "//android.widget.ImageView[9]")
    public AndroidElement startButton;

    @AndroidFindBy(id = "com.gigantic.clawee:id/playImage")
    public AndroidElement blueButton;

    public MobilePage swipeUp(){
        AndroidTouchAction swipe = new AndroidTouchAction(driver)
                .longPress(
                        LongPressOptions
                                .longPressOptions()
                                .withPosition(
                                        PointOption.point( 997, 619)))
                .moveTo(PointOption.point( 997, 988))
                .release();
        swipe.perform();
        return this;
    }
    public MobilePage swipeDown(){
        AndroidTouchAction swipe = new AndroidTouchAction(driver)
                .longPress(
                        LongPressOptions
                                .longPressOptions()
                                .withPosition(
                                        PointOption.point(997, 988 )))
                .moveTo(PointOption.point( 997, 619))
                .release();
        swipe.perform();
        return this;
    }
}
