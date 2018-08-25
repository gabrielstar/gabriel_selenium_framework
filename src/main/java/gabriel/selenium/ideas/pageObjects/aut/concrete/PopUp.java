package gabriel.selenium.ideas.pageObjects.aut.concrete;

import gabriel.selenium.ideas.pageObjects.basic.BasePO;
import gabriel.selenium.ideas.utilities.Utilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class PopUp extends BasePO {

    @FindBy(className = "cmp-popup_content")
    static WebElement popUpFrame;
    //<button class="cmp-button_button cmp-intro_acceptAll "><span class="">Przejdź do serwisu</span></button>

    //old way  for stale exception acceptButton = driver.findElement(By.className("cmp-intro_acceptAll"));
    @FindBy(className = "cmp-intro_acceptAll ")
    static WebElement acceptButton;

    public PopUp(){
        //no super constructor - seems factory does init either way

    }
    @Override
    public void setElementWait(int elementWait) {
        ; //default
    }

    @Override
    public void setURL(String URL) {
        this.URL = null;
    }

    public WebElement getPopUpFrame(){
        return popUpFrame;
    }
    public void clickAcceptButton(){
        acceptButton.click();
    }
    public void waitToLoad() throws Exception {
        Utilities.waitForElementVisible(popUpFrame,elementWait);
    }
    public boolean isLoaded(){
        if(acceptButton!=null && popUpFrame!=null){
            return true;
        }
        return false;
    }

}
