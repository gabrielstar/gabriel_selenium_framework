package gabriel.selenium.ideas.pageObjects.aut.basic;

import gabriel.selenium.ideas.pageObjects.aut.concrete.PopUp;
import gabriel.selenium.ideas.pageObjects.basic.BasePO;
import gabriel.selenium.ideas.utilities.Utilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class OnetBasePagePO<M extends WebElement> extends BasePO{

    @RequiredOnPageLoad
    @FindBy(className = "onetLogo")
    protected M onetLogo;

    @RequiredOnPageLoad
    @FindBy(id = "mainPageFooter")
    protected M footer;

    static protected PopUp popUp = new PopUp();

    public OnetBasePagePO(){
        super();
        this.setPageTitle("onet.");

    }

    public PopUp getPopUp(){
        return this.popUp;
    }
    public boolean  hasPopUp(){
        return this.popUp.isLoaded();
    }
    public void  acceptPopUp(){
         this.popUp.clickAcceptButton();
    }
    public void waitForPopUpToLoad() throws Exception {
        this.popUp.waitToLoad();
    }
    public void clickOnetLogo() throws Exception {
        Utilities.waitForElementClickable(onetLogo,elementWait);
        if(onetLogo.isDisplayed()) {
            onetLogo.click();
        }
    }
    public boolean isAtOnetPages(){
        //without protocol - https or http
        System.out.println(getDriver().getCurrentUrl());
        return getDriver().getCurrentUrl().contains("onet.pl");
    }


}
