package gabriel.selenium.ideas.pageObjects.basic;

import gabriel.selenium.ideas.pageObjects.aut.basic.RequiredElementTimeoutException;
import gabriel.selenium.ideas.pageObjects.aut.basic.RequiredOnPageLoad;
import gabriel.selenium.ideas.utilities.CreateDriver;
import gabriel.selenium.ideas.utilities.Global_VARS;
import gabriel.selenium.ideas.utilities.Utilities;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePO<M extends WebElement> {

    protected int elementWait = Global_VARS.TIMEOUT_ELEMENT;
    protected String pageTitle = "UNSPECIFIED_PAGE";
    protected String URL;
    protected WebDriver driver = CreateDriver.getInstance().getDriver();

    public BasePO() {
        PageFactory.initElements(driver, this);
    }

    /**
     * @param elementWait - default page timeout used in waits
     *                    implement as empty to use defaults
     */
    //abstract methods - must be implemented
    public abstract void setElementWait(int elementWait);

    public abstract void setURL(String URL);

    /**
     * Method which when executed waits on required and user specified elements.
     * User can use waitForRequiredElementsToLoad as base.
     *
     * @throws Exception
     */
    public abstract void waitToLoad() throws Exception;
    public abstract boolean isLoaded() throws Exception;
    public int getElementWait() {
        return this.elementWait;
    }

    public String getURL() {
        return this.URL;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageTitle() {
        return this.getPageTitle();
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void visit() {
        if (!URL.isEmpty()) {
            try {
                getDriver().navigate().to(this.URL);
            } catch (Exception e) {
                System.out.println("Failed to navigate to " + this.URL + ". Make sure it is correct.");
            }
        } else {
            System.out.println("Cannot navigate to empty URL");
        }

    }

    /**
     * Method returns all fields that belong to class whether they are declared in class itself or any super class
     * (derived). Then one can for instance loop over all fields and see which one are annotated.
     *
     * @param classObject
     * @return list of fields that are declared within class
     */
    public Field[] getAllRequiredElements(Class<?> classObject) {
        //Object means we reached the end - null check is just for certainty as all classes derive from Object
        if (classObject.getSuperclass() != null) {
            if (classObject.getSuperclass().getSimpleName().equals("Object")) {
                return classObject.getDeclaredFields();
            } else {
                return (Field[]) ArrayUtils.addAll(classObject.getDeclaredFields(),
                        getAllRequiredElements(classObject.getSuperclass()));
            }
        }
        return null;
    }

    /**
     * Method iterates over all WebELement fields in a class and waits for given WebElement
     * if it was annotated as @RequiredOnPageLoad
     *
     * @throws Exception
     */
    public void waitForRequiredElementsToLoad() throws Exception {
        for (Field field : getAllRequiredElements(this.getClass())) {
            field.setAccessible(true); //switch off so we can get all fields ignoring access rules
            if (field.isAnnotationPresent(RequiredOnPageLoad.class)) {
                if (field.getType().getSimpleName().equals(WebElement.class.getSimpleName())) {
                    System.out.println("WebElement " + field.getName().toString() + " is required. Waiting ... ");
                    try {
                        Utilities.waitForElementVisible((WebElement) field.get(this), elementWait);
                    } catch (TimeoutException e) {
                        System.out.println("Element " + field.getName() + " has been marked as required but is not found after "
                                + elementWait + "s");
                        RequiredElementTimeoutException exception = new RequiredElementTimeoutException(e);
                        System.out.println(e.getMessage());
                        throw exception;
                    } catch (Exception e) {
                    }
                } else {
                    System.out.println("Field <" + field.getType().getSimpleName() + ">: " + field.getName().toString() + " is required but type is not WebElement. Ignoring...");

                }
            }

        }
    }

    /**
     * @throws Exception method verifies that all annotated elements are present in DOM tree
     *                   we do not filter in getAllRequiredElements function by WebElement in case we want to
     *                   further extend to fire waitToLoad on Page Objects here
     */
    public boolean isAllRequiredElementsLoaded() throws Exception {
        for (Field field : getAllRequiredElements(this.getClass())) {
            field.setAccessible(true); //switch off so we can get all fields ignoring access rules
            if (field.isAnnotationPresent(RequiredOnPageLoad.class)) {
                if (field.getType().getSimpleName().equals(WebElement.class.getSimpleName())) {
                    try {
                        if (((WebElement) field.get(this)) != null
                                && ((WebElement) field.get(this)).isDisplayed()) {
                            ;
                        } else {
                            System.out.println("WebElement " + field.getName().toString() + " is not loaded but is annotated as required.");
                            return false;
                        }

                    } catch (Exception e) {
                    }
                } else {
                    System.out.println("Field <" + field.getType().getSimpleName() + ">: " + field.getName().toString() + " is required but type is not WebElement. Ignoring...");

                }
            }

        }
        return true;
    }

}
