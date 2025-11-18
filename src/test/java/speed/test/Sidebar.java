package speed.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Sidebar {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public Sidebar(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openAnalysisFnppRegistry() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/projectAnalysis']")));
        link.click(); // TODO: proanalizirovat rolspec menu pered klikom
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.MuiTable-root")));
    }

    public void openMyTasks() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/userTasks']")));
        link.click(); // TODO: dobavit proverku prava dostupa dlya vzvoda
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ant-tabs")));
    }

    public void openTdRegistry() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/registerOfTD']")));
        link.click(); // TODO: proanalizirovat perehod v reestr TD
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.MuiTable-root")));
    }
}
