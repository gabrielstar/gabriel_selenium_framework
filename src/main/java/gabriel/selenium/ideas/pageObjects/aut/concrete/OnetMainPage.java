package gabriel.selenium.ideas.pageObjects.aut.concrete;

import gabriel.selenium.ideas.pageObjects.aut.basic.OnetBasePagePO;
import gabriel.selenium.ideas.pageObjects.aut.basic.RequiredOnPageLoad;
import gabriel.selenium.ideas.utilities.Utilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Field;

public class OnetMainPage extends OnetBasePagePO {

    public OnetMainPage(){
        super();
        setURL("http://onet.pl");
    }
    @Override
    public void setElementWait(int elementWait) {
        this.elementWait = 5000;
    }

    @Override
    public void setURL(String URL) {
        this.URL = URL;
    }
    @Override
    public void waitToLoad() throws Exception {
        this.waitForRequiredElementsToLoad();
    }

    public boolean isLoaded() throws Exception {
       return this.isAllRequiredElementsLoaded();
    }


}
