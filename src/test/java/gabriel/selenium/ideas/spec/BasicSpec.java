package gabriel.selenium.ideas.spec;

import gabriel.selenium.ideas.pageObjects.aut.basic.RequiredElementTimeoutException;
import gabriel.selenium.ideas.pageObjects.aut.concrete.OnetMainPage;
import gabriel.selenium.ideas.utilities.Utilities;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class BasicSpec extends BasicAbstractSpec {
    private static OnetMainPage onetMainPage = new OnetMainPage();

    @Test
    public void basicTest() throws Exception {

        onetMainPage.visit();
        try {
            onetMainPage.waitForPopUpToLoad();
            if (onetMainPage.hasPopUp()) {
                onetMainPage.acceptPopUp();
            }
        } catch (RequiredElementTimeoutException e) {
            ; //is one off
        }
        if (onetMainPage.isLoaded()) {
            System.out.println("is loaded");
        } else {
            System.out.println("is not loaded");
        }

        onetMainPage.clickOnetLogo();

        onetMainPage.getDriver().switchTo().defaultContent(); //another popUp
        assert onetMainPage.isAtOnetPages() == true;

    }
}
