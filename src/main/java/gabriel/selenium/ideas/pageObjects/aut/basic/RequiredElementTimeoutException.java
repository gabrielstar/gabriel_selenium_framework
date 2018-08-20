package gabriel.selenium.ideas.pageObjects.aut.basic;

import java.util.concurrent.TimeoutException;

public class RequiredElementTimeoutException extends TimeoutException {
    org.openqa.selenium.TimeoutException timeoutException;
    @Override
    public String toString() {
        return "RequiredElementTimeoutException{}";
    }
    public RequiredElementTimeoutException(org.openqa.selenium.TimeoutException exception){
        this.timeoutException = exception;
    }

    @Override
    public String getMessage() {
        return timeoutException.getMessage();
    }
}
