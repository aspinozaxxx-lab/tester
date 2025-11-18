package speed.test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MyTasksPage {

    private static final By POPOVER_LOCATOR = By.cssSelector("div.ant-popover");
    private static final By ROWS_LOCATOR = By.cssSelector("table tbody tr");
    private static final By EMPTY_STATE_LOCATOR = By.cssSelector("div.ant-empty");
    private static final By EMPTY_TEXT_LOCATOR = By.xpath("//*[contains(normalize-space(text()), 'Нет данных')]");

    private final WebDriver driver;
    private final WebDriverWait fastWait;
    private final WebDriverWait tableWait;

    public MyTasksPage(WebDriver driver) {
        this.driver = driver;
        this.fastWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.tableWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void openIncomingTab() {
        WebElement tab = fastWait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div.ant-tabs-tab[data-node-key='inbox']")));
        tab.click(); // TODO: dobavit proverku sostoyaniya tabov
        fastWait.until(ExpectedConditions.visibilityOfElementLocated(ROWS_LOCATOR));
    }

    public void setProcessFilter(String value) {
        applyFilterValue(FilterColumn.PROCESS, value);
    }

    public void setStageFilter(String value) {
        applyFilterValue(FilterColumn.STAGE, value);
    }

    public void applyFilter() {
        // pustoj rezultat tozhe schitaetsja uspehom filtra
        tableWait.until(currentDriver -> {
            boolean hasRows = !currentDriver.findElements(ROWS_LOCATOR).isEmpty();
            boolean hasEmptyBlock = !currentDriver.findElements(EMPTY_STATE_LOCATOR).isEmpty();
            boolean hasEmptyText = !currentDriver.findElements(EMPTY_TEXT_LOCATOR).isEmpty();
            return hasRows || hasEmptyBlock || hasEmptyText;
        });
    }

    private void openFilterPopover(FilterColumn column) {
        WebElement trigger = fastWait.until(ExpectedConditions.elementToBeClickable(column.getButtonLocator()));
        trigger.click();
        fastWait.until(ExpectedConditions.visibilityOfElementLocated(POPOVER_LOCATOR));
    }

    private WebElement currentPopover() {
        return fastWait.until(ExpectedConditions.visibilityOfElementLocated(POPOVER_LOCATOR));
    }

    private void applyFilterValue(FilterColumn column, String value) {
        openFilterPopover(column);
        WebElement popover = currentPopover();
        WebElement input = popover.findElement(By.cssSelector("input.ant-input"));
        input.clear();
        input.sendKeys(value);
        input.sendKeys(Keys.ENTER);
    }

    private enum FilterColumn {
        PROCESS("\u041f\u0440\u043e\u0446\u0435\u0441\u0441"),
        STAGE("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u044d\u0442\u0430\u043f\u0430");

        private final By buttonLocator;

        FilterColumn(String headerText) {
            this.buttonLocator = By.xpath("//th[.//span[contains(normalize-space(.), '" + headerText + "')]]//button");
        }

        public By getButtonLocator() {
            return buttonLocator;
        }
    }
}
