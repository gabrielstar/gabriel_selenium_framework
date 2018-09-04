package gabriel.selenium.ideas.utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.sql.Driver;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Gab
 * <p>
 * Selenium Driver Class
 */
public class CreateDriver {
    // local variables - this means that all objects of CreateDriver will share same instance
    //this is class variable
    private static CreateDriver instance = null;
    //sometimes this is notated as public by convention
    private static final int IMPLICIT_TIMEOUT = 0;

    //normally that would be without threads but we want to run some tests in parallel
    //this will make copies of thise variables per each thread?
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();
    private ThreadLocal<String> sessionBrowser = new ThreadLocal<String>();
    private ThreadLocal<String> sessionPlatform = new ThreadLocal<String>();
    private ThreadLocal<String> sessionVersion = new ThreadLocal<String>();
    private String getEnv = null;
    private Properties props = new Properties();


    // constructor - private so you cannot instantiate from outside of class
    private CreateDriver() {
        parseProperties();
    }
    public Properties getProps(){
        return this.props;
    }
    /**
     * 1. reads properties from default file and
     * 2. merges final result with platform specific file if exists and
     * 3. merges final result with command line properties
     */
    private void parseProperties() {
        //properties file is static so makes sense to load only once
        try {
            System.out.println(Global_VARS.SE_PROPS);
            //1.loading props file
            props.load(new FileInputStream(Global_VARS.SE_PROPS));
            //2.loading platform specific file - can be overriden in command line,causes extra props to load
            String platform = props.getProperty("platform");
            if (System.getProperty("platform") != null) {
                platform = System.getProperty("platform");
            } else if (platform == null) {
                platform = Global_VARS.DEF_PLATFORM;
            }
            if (platform != null) {
                System.out.println("Loading properties for platform: " + platform);
                String platformFilePath = Global_VARS.propFileDir + platform + ".properties";
                File propFile = new File(platformFilePath);
                Properties envProps = new Properties();
                if (propFile.exists() && propFile.isFile()) {
                    try {
                        envProps.load(new FileInputStream(platformFilePath));
                        props.putAll(envProps);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else {
                    System.out.println("Platform specific properties for " + platform + " do not exist");
                }
            } else {
                System.out.println("Platform property is not set");
            }
            //3. merging command lines attributes
            if (System.getProperty("properties") != null) {
                System.out.println("Properties given on the commandline: " + System.getProperty("properties"));
                Properties jvmProps = new Properties();
                String[] tmpProps = System.getProperty("properties").split(";");
                for (String prop : tmpProps) {
                    try {
                        String key = prop.split("=")[0];
                        String value = prop.split("=")[1];
                        jvmProps.setProperty(key, value);
                        System.out.println("\t\n " + key + "-> " + value);
                    } catch (Exception e) {
                        System.out.println("Thrown:" + e.toString() + "on " + prop);
                        System.out.println("Unable to parse JVM properties args. Sure they are Dprops='key=value;key2=value2; form?");
                    }
                }
                props.putAll(jvmProps); //merge as this is HashMap
                System.out.println("Merged final set of properties:");
                for (Map.Entry<Object, Object> property : props.entrySet()) {
                    System.out.println("\t\n" + property.getKey() + "-> " + property.getValue());
                }

            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * getInstance method to retrieve active driver instance
     * this is the only way to create object too
     * method is static so it can only reference static variables
     *
     * @return CreateDriver
     */
    //parametrize with browser to run
    public static CreateDriver getInstance() {
        if (instance == null) {
            instance = new CreateDriver();
            Map<String, Object> opts = new HashMap<String, Object>();
            String browser = null;
            if (instance.props.containsKey("browser")) {
                browser = instance.props.getProperty("browser");
            }
            System.out.println("Browser: " + browser);
            try {
                if (!(browser == null)) {
                    if (browser.equals("suite")) {
                        ; //no browser set - suites will need to set
                        System.out.println("Setting no browser - suites need to set explicitely");
                    } else {//repeated code - put in helper method/class
                        System.out.println("Setting browser " + browser);
                        String env = Global_VARS.DEF_ENVIRONMENT;
                        if(instance.props.getProperty("environment")!=null){
                            env = instance.props.getProperty("environment");
                        }
                        instance.setDriver(browser, Global_VARS.DEF_PLATFORM, env, opts);
                    }
                } else {//default
                    System.out.println("Setting default browser");
                    instance.setDriver(Global_VARS.DEF_BROWSER, Global_VARS.DEF_PLATFORM, Global_VARS.DEF_ENVIRONMENT, opts);
                }
            } catch (Exception e) {
                System.out.println(" Exception: " + e.getStackTrace());

            }

        }
        return instance;
    }

    /**
     * setDriver method to create driver instance
     *
     * @param browser
     * @param environment
     * @param platform
     * @param optPreferences
     * @throws Exception
     */
    @SafeVarargs
    public final void setDriver(String browser,
                                String platform,
                                String environment,
                                Map<String, Object>... optPreferences)
            throws Exception {

        DesiredCapabilities caps = null;
        String localHub = "http://127.0.0.1:4273/wd/hub";
        String getPlatform = null;

        switch (browser) {
            case "firefox":
                caps = DesiredCapabilities.firefox();

                FirefoxOptions ffOpts = new FirefoxOptions();
                FirefoxProfile ffProfile = new FirefoxProfile();

                ffProfile.setPreference("browser.autofocus", true);
                ffProfile.setPreference("browser.tabs.remote.autostart.2", false);

                caps.setCapability(FirefoxDriver.PROFILE, ffProfile);
                caps.setCapability("marionette", true);

                // then pass them to the local WebDriver
                if (environment.equalsIgnoreCase("local")) {
                    System.setProperty("webdriver.gecko.driver", props.getProperty("gecko.driver.windows.path"));
                    webDriver.set(new FirefoxDriver(ffOpts.merge(caps)));
                }

                break;
            case "chrome":
                caps = DesiredCapabilities.chrome();

                ChromeOptions chOptions = new ChromeOptions();
                Map<String, Object> chromePrefs = new HashMap<String, Object>();

                chromePrefs.put("credentials_enable_service", false);

                chOptions.setExperimentalOption("prefs", chromePrefs);
                chOptions.addArguments("--disable-plugins", "--disable-extensions", "--disable-popup-blocking");

                caps.setCapability(ChromeOptions.CAPABILITY, chOptions);
                caps.setCapability("applicationCacheEnabled", false);
                if (optPreferences.length > 0) {
                    processCHOptions(chOptions, optPreferences);
                }
                if (environment.equalsIgnoreCase("local")) {
                    System.setProperty("webdriver.chrome.driver", props.getProperty("chrome.driver.windows.path"));
                    webDriver.set(new ChromeDriver(chOptions.merge(caps)));
                } else if (environment.equalsIgnoreCase("remote")) {
                    String remoteHubURL = instance.props.getProperty("grid");
                    caps.setCapability("browserName",
                            browser);
//                    caps.setCapability("version",
//                            caps.getVersion());
//                    caps.setCapability("platform",
//                            platform);
                    webDriver.set(new RemoteWebDriver(new URL(remoteHubURL), caps));
                    ((RemoteWebDriver) webDriver.get()).setFileDetector(
                            new LocalFileDetector());
                }

                break;
            case "internet explorer":
                caps = DesiredCapabilities.internetExplorer();

                InternetExplorerOptions ieOpts = new InternetExplorerOptions();

                ieOpts.requireWindowFocus();
                ieOpts.merge(caps);
                caps.setCapability("browserName",browser);
                caps.setCapability("requireWindowFocus", true);
                //sometimes iedriverServer starts with non 100% zoom, we can ignore that but then we need to reset zoom on real URL
                //caps.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
                //also make sure in securit tabs all site cats have protected mode on ; lol!
                if (environment.equalsIgnoreCase("local")) {
                    System.setProperty("webdriver.ie.driver", props.getProperty("ie.driver.windows.path"));
                    try {
                        webDriver.set(new InternetExplorerDriver(ieOpts.merge(caps)));
                    } catch (Exception e) {
                        System.out.println(" Exception " + e.toString());
                    }
                }else if (environment.equalsIgnoreCase("remote")) {
                    String remoteHubURL = instance.props.getProperty("grid");
                    System.out.println("STarting ie");
//                    caps.setCapability("version",
//                            caps.getVersion());
//                    caps.setCapability("platform",
//                            platform);
                    webDriver.set(new RemoteWebDriver(new URL(remoteHubURL), caps));
                    ((RemoteWebDriver) webDriver.get()).setFileDetector(
                            new LocalFileDetector());
                }

                break;

            case "edge":
                caps = DesiredCapabilities.edge();

                EdgeOptions edgeOpts = new EdgeOptions();

                edgeOpts.merge(caps);

                caps.setCapability("requireWindowFocus", true);

                if (environment.equalsIgnoreCase("local")) {
                    try {
                        System.setProperty("webdriver.edge.driver", props.getProperty("edge.driver.windows.path"));
                        webDriver.set(new EdgeDriver(edgeOpts.merge(caps)));
                    } catch (Exception e) {
                        System.out.println("e: " + e.toString());
                    }
                }

                break;
        }

        getEnv = environment;
        getPlatform = platform;
        sessionId.set(((RemoteWebDriver) webDriver.get()).getSessionId().toString());
        sessionBrowser.set(caps.getBrowserName());
        sessionVersion.set(caps.getVersion());
        sessionPlatform.set(getPlatform);

        System.out.println("\n*** TEST ENVIRONMENT = "
                + getSessionBrowser().toUpperCase()
                + "/" + getSessionPlatform().toUpperCase()
                + "/" + getEnv.toUpperCase()
                + "/Selenium Version=" + props.getProperty("selenium.revision")
                + "/Session ID=" + getSessionId() + "\n");

        getDriver().manage().timeouts().implicitlyWait(IMPLICIT_TIMEOUT, TimeUnit.SECONDS);
        getDriver().manage().window().maximize();
    }

    /**
     * overloaded setDriver method to switch driver to specific WebDriver
     * if running concurrent drivers
     *
     * @param driver WebDriver instance to switch to
     */
    public void setDriver(WebDriver driver) {
        webDriver.set(driver);

        sessionId.set(((RemoteWebDriver) webDriver.get())
                .getSessionId().toString());

        sessionBrowser.set(((RemoteWebDriver) webDriver.get())
                .getCapabilities().getBrowserName());

        sessionPlatform.set(((RemoteWebDriver) webDriver.get())
                .getCapabilities().getPlatform().toString());

        //setBrowserHandle(getDriver().getWindowHandle());
    }

    /**
     * getDriver method to retrieve active driver
     *
     * @return WebDriver
     */
    public WebDriver getDriver() {
        return webDriver.get();
    }

    /**
     * closeDriver method to close active driver
     */
    public void closeDriver() {
        try {
            getDriver().quit();
        } catch (Exception e) {
            // do something
        }
    }

    /**
     * getSessionId method to retrieve active id
     *
     * @return String
     * @throws Exception
     */
    public String getSessionId() throws Exception {
        return sessionId.get();

    }

    /**
     * getSessionBrowser method to retrieve active browser
     *
     * @return String
     * @throws Exception
     */
    public String getSessionBrowser() throws Exception {
        return sessionBrowser.get();
    }

    /**
     * getSessionVersion method to retrieve active version
     *
     * @return String
     * @throws Exception
     */
    public String getSessionVersion() throws Exception {
        return sessionVersion.get();
    }

    /**
     * getSessionPlatform method to retrieve active platform
     *
     * @return String
     * @throws Exception
     */
    public String getSessionPlatform() throws Exception {
        return sessionPlatform.get();
    }

    /**
     * driverWait method pauses the driver in seconds
     *
     * @param seconds to pause
     */
    public void driverWait(long seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            // do something
        }
    }

    /**
     * driverRefresh method reloads the current browser page
     */
    public void driverRefresh() {
        getDriver().navigate().refresh();
    }

    /**
     * Process Chrome Options method to override default browser
     * driver behavior
     *
     * @param chOptions - the ChromeOptions object
     * @param options   - the key: value pair map
     * @throws Exception
     */
    private void processCHOptions(ChromeOptions chOptions, Map<String, Object>[] options) throws Exception {
        for (int i = 0; i < options.length; i++) {
            Object[] keys = options[i].keySet().toArray();
            Object[] values = options[i].values().toArray();

            // same as Desired Caps except the following difference

            for (int j = 0; j < keys.length; j++) {
                if (values[j] instanceof Integer) {
                    values[j] = (int) values[j];
                    chOptions.setExperimentalOption("prefs", options[i]);
                }

                // etc...
            }
        }
    }
}