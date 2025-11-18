package speed.test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TdRegistryPage {

    private static final By POPOVER_LOCATOR = By.cssSelector("div.ant-popover");
    private static final By DROPDOWN_LOCATOR = By.cssSelector("div.ant-select-dropdown:not(.ant-select-dropdown-hidden)");
    private static final By ROWS_LOCATOR = By.cssSelector("table tbody tr");
    private static final By EMPTY_STATE_LOCATOR = By.cssSelector("div.ant-empty");
    private static final By EMPTY_TEXT_LOCATOR = By.xpath("//*[contains(normalize-space(text()), 'Нет данных')]");
    private static final By TYPE_FILTER_BUTTON = By.xpath("//th[.//span[contains(normalize-space(.), '\u0412\u0438\u0434')]]//button");

    private final WebDriver driver;
    private final WebDriverWait fastWait;
    private final WebDriverWait dropdownWait;
    private final WebDriverWait tableWait;

    public TdRegistryPage(WebDriver driver) {
        this.driver = driver;
        this.fastWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.dropdownWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.tableWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void setTypeFilterDab() {
        openTypeFilterPopover();
        WebElement popover = currentPopover();
        WebElement select = popover.findElement(By.cssSelector("div.ant-select"));
        select.click();

        dropdownWait.until(ExpectedConditions.visibilityOfElementLocated(DROPDOWN_LOCATOR));
        // posle otkrytija vypadajushchego spiska vibiraem pervuju opciyu cherez aktivnyj element
        WebElement active = driver.switchTo().activeElement();
        active.sendKeys(Keys.ARROW_DOWN);
        active.sendKeys(Keys.ENTER);
    }

    public void applyFilter() {
        // pustoj rezultat tozhe schitaetsja uspehom filtra
        tableWait.until(d -> {
            boolean hasRows = !d.findElements(ROWS_LOCATOR).isEmpty();
            boolean hasEmptyBlock = !d.findElements(EMPTY_STATE_LOCATOR).isEmpty();
            boolean hasEmptyText = !d.findElements(EMPTY_TEXT_LOCATOR).isEmpty();
            return hasRows || hasEmptyBlock || hasEmptyText;
        });
    }

    private void openTypeFilterPopover() {
        WebElement trigger = fastWait.until(ExpectedConditions.elementToBeClickable(TYPE_FILTER_BUTTON));
        trigger.click();
        fastWait.until(ExpectedConditions.visibilityOfElementLocated(POPOVER_LOCATOR));
    }

    private WebElement currentPopover() {
        return fastWait.until(ExpectedConditions.visibilityOfElementLocated(POPOVER_LOCATOR));
    }
}
