package gabriel.selenium.ideas.spec;


import gabriel.selenium.ideas.utilities.CreateDriver;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

public abstract class BasicAbstractSpec {
    /*
        Will be available in extended classes
     */
    protected WebDriver driver;
    /**
     * @throws Exception
     * creates a local reference to the driver from singleton instance
     * by default driver strts with chrome options
     */
    @BeforeClass
    public void before1_initialize() throws Exception {
        System.out.println("Instantiating driver for class: " + this.getClass().getSimpleName());
        try {
            driver = CreateDriver.getInstance().getDriver();
        } catch (Exception e) {
            System.out.println("[ERROR]....." + e.getStackTrace().toString());
        }
    }

    /**
     * Very basic information on start up for all tests
     * order of loading functions with @BeforeClass annotation matters - alphabetical
     */
    @BeforeClass
    public void before2_logBasicInfo(){
        System.out.println("Class: " + this.getClass().getName());
        try {
            System.out.println("Driver session ID: " + gabriel.selenium.ideas.utilities.CreateDriver.getInstance().getSessionId());
            System.out.println("Driver browser: "  + CreateDriver.getInstance().getSessionBrowser());
            System.out.println("Driver env: "  + CreateDriver.getInstance().getSessionVersion());
            System.out.println("Driver platform: "  + CreateDriver.getInstance().getSessionPlatform());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }catch(Exception e){
            System.out.println("Unknown exception: " + e.toString());
        }
    }
    @BeforeTest
    public void logTestName(){
        System.out.println("Running test from: " + this.getClass().getSimpleName());
    }
    @AfterClass
    public void killDriver() throws Exception {
        System.out.println("Class: " + this.getClass().getSimpleName());
        System.out.println("Killing browser");
        if (driver != null){
            CreateDriver.getInstance().closeDriver();
        }
    }
}
