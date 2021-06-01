package services;

import lombok.extern.log4j.Log4j;
import org.testng.annotations.*;

@Log4j
public class BaseTest {


    @Parameters({"platform","env"})
    @BeforeSuite
    public void startTestSuite() {

    }

    @AfterTest
    public void finishTest(){
    }

    private String getGrid(String gridValue, String port) {
        return "http://" + gridValue + ":" + port + "/wd/hub";
    }

}
