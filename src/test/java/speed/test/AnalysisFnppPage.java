package speed.test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AnalysisFnppPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public AnalysisFnppPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openFirstAnalysisCard() {
        WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[@aria-label='eye'])[1]/ancestor::button")));
        viewButton.click(); // TODO: obrabotat situaciyu otsutstviya zapisey
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ant-drawer-content")));
    }

    public void openExportDialog() {
        WebElement exportButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'Выгрузить отчет')]/ancestor::button")));
        exportButton.click(); // TODO: proverit nalichie prav na vygruzku
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ant-modal-content")));
    }

    public void selectInternalTableCheckbox() {
        By checkboxLocator = By.xpath("//p[contains(text(),'Анализ проекта ФНП. Внутренняя таблица')]/following-sibling::label//input");
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(checkboxLocator));
        if (!checkbox.isSelected()) {
            checkbox.click(); // TODO: obrabotat osobennosti UI pri dvuh klikah
        }
    }

    public void selectXlsxFormat() {
        By selectLocator = By.xpath("//p[contains(text(),'Анализ проекта ФНП. Внутренняя таблица')]/following-sibling::div[contains(@class,'ant-select')]");
        WebElement select = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
        WebElement currentValue = select.findElement(By.cssSelector("span.ant-select-selection-item"));
        if (currentValue.getText().contains("XLSX")) {
            return; // TODO: format uzhe vybran
        }
        select.click();
        WebElement dropdownInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.ant-select-dropdown input.ant-select-selection-search-input")));
        dropdownInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        dropdownInput.sendKeys("XLSX");
        dropdownInput.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.textToBePresentInElement(currentValue, "XLSX"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.ant-select-dropdown")));
    }

    public void exportReport() {
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ant-modal-content")));
        WebElement exportConfirm = modal.findElement(By.xpath(".//span[text()='Выгрузить']/ancestor::button"));
        exportConfirm.click(); // TODO: dobavit kontrol statusa zaprosa na server
        wait.until(ExpectedConditions.stalenessOf(modal));
    }
}
