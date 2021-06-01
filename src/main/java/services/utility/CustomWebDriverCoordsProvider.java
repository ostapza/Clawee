package services.utility;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.ashot.coordinates.Coords;
import ru.yandex.qatools.ashot.coordinates.CoordsProvider;

public class CustomWebDriverCoordsProvider extends CoordsProvider {
    public CustomWebDriverCoordsProvider() {
    }

    public Coords ofElement(WebDriver driver, WebElement element) {
        Point point = element.getLocation();
        Dimension dimension = element.getSize();
        return new Coords(point.getX(), point.getY(), dimension.getWidth() + 150, dimension.getHeight() + 150);
    }
}

