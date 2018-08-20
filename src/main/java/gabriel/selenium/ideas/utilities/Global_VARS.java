package gabriel.selenium.ideas.utilities;

import java.io.File;

/**
 * @author Carl Cocchiaro
 *
 * Global Variable Utility Class
 *
 */
public class Global_VARS {
    // browser defaults
    public static final String BROWSER = "chrome";
    public static final String PLATFORM = "Windows 7";
    public static final String ENVIRONMENT = "local";
    public static String DEF_BROWSER = "chrome";
    public static String DEF_PLATFORM = "uat";
    public static String DEF_ENVIRONMENT = "local";

    // suite folder defaults
    public static String SUITE_NAME = null;
    public static final String TARGET_URL = "http://www.practiceselenium.com/";
    public static String propFileDir = "src/main/java/gabriel/selenium/ideas/utilities/";
    public static String propFileName = "selenium.properties";
    public static final String SE_PROPS = new File(propFileDir+propFileName).getAbsolutePath();
    public static final String TEST_OUTPUT_PATH = "test-output/";
    public static final String LOGFILE_PATH = TEST_OUTPUT_PATH + "Logs/";
    public static final String REPORT_PATH = TEST_OUTPUT_PATH + "Reports/";
    public static final String REPORT_CONFIG_FILE = "src/main/java/com/gabriel.selenium.ideas/extent-config.xml";

    // suite timeout defaults
    public static final int TIMEOUT_MINUTE = 60;
    public static final int TIMEOUT_ELEMENT = 10;
}
