package gabriel.selenium.ideas.utilities;

import jdk.nashorn.internal.runtime.ECMAException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utilities {
    public static void waitForElementVisible(By by, int timer) throws Exception{
        WebDriver driver = CreateDriver.getInstance().getDriver();
        WebDriverWait wait = new WebDriverWait(driver,timer);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }
    public static void waitForElementVisible(WebElement element, int timer) throws Exception{
        WebDriver driver = CreateDriver.getInstance().getDriver();
        WebDriverWait wait = new WebDriverWait(driver,timer);
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    public static void waitForElementClickable(WebElement element, int timer) throws Exception{
        WebDriver driver = CreateDriver.getInstance().getDriver();
        WebDriverWait wait = new WebDriverWait(driver,timer);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
}
