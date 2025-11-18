package speed.test;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ExpertAesUiTest extends BaseUiTest {

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(10);
    private static final String PROCESS_FILTER_VALUE = "Ввод нового";
    private static final String STAGE_FILTER_VALUE = "Анализ соответствия";
    private static final By EMPTY_STATE_LOCATOR = By.cssSelector("div.ant-empty");
    private static final By EMPTY_TEXT_LOCATOR = By.xpath("//*[contains(normalize-space(text()), 'Нет данных')]");

    @Test
    public void testExportFnppAnalysisReport() {
        // TODO: proiti polnyj flow vygruzki analiza FNP
        Sidebar sidebar = login();
        sidebar.openAnalysisFnppRegistry();

        AnalysisFnppPage page = new AnalysisFnppPage(driver);
        page.openFirstAnalysisCard();
        page.openExportDialog();
        page.selectInternalTableCheckbox();
        page.selectXlsxFormat();
        page.exportReport();

        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT);
        boolean modalClosed = wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("div.ant-modal-content")));
        Assert.assertTrue("Export modal should be closed after submitting report", modalClosed);
    }

    @Test
    public void testMyTasksIncomingSearchByProcessAndStage() {
        // TODO: proverit filtry v razdele Moi zadachi
        Sidebar sidebar = login();
        sidebar.openMyTasks();

        MyTasksPage page = new MyTasksPage(driver);
        page.openIncomingTab();
        page.setProcessFilter(PROCESS_FILTER_VALUE);
        page.setStageFilter(STAGE_FILTER_VALUE);
        page.applyFilter();

        WebElement table = driver.findElement(By.cssSelector("table"));
        int processIndex = findColumnIndex(table, "Процесс");
        int stageIndex = findColumnIndex(table, "Название этапа");
        Assert.assertTrue("Process column should exist", processIndex >= 0);
        Assert.assertTrue("Stage column should exist", stageIndex >= 0);

        List<WebElement> rows = getBodyRows(table);
        boolean processFilterActive = isFilterActive("Процесс");
        boolean stageFilterActive = isFilterActive("Название этапа");

        Assert.assertTrue("Process filter should be active", processFilterActive);
        Assert.assertTrue("Stage filter should be active", stageFilterActive);
        if (rows.isEmpty()) {
            Assert.assertTrue("Empty state should be visible when MyTasks filters return no rows",
                    isEmptyStateVisible());
            return;
        }

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            if (cells.size() <= Math.max(processIndex, stageIndex)) {
                continue; // TODO: obrabotat sluchai tekhnicheskih strok
            }
            String processText = cells.get(processIndex).getText();
            String stageText = cells.get(stageIndex).getText();
            Assert.assertTrue("Process cell should match filter",
                    processText.contains(PROCESS_FILTER_VALUE));
            Assert.assertTrue("Stage cell should match filter",
                    stageText.contains(STAGE_FILTER_VALUE));
        }
    }

    @Test
    public void testTdRegistryFilterByTypeDab() {
        // TODO: proverit filtr po kolonce Vid v reestre TD
        Sidebar sidebar = login();
        sidebar.openTdRegistry();

        TdRegistryPage page = new TdRegistryPage(driver);
        page.setTypeFilterDab();
        page.applyFilter();

        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT);
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table")));

        int typeIndex = findColumnIndex(table, "Вид");
        Assert.assertTrue("Type column should exist", typeIndex >= 0);

        List<WebElement> rows = getBodyRows(table);
        if (rows.isEmpty()) {
            Assert.assertTrue("Empty state should be visible when TD filter returns no rows",
                    isEmptyStateVisible());
            return;
        }

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            if (cells.size() <= typeIndex) {
                continue; // TODO: propustit sluzhebnye stroki bez dannyh
            }
            String typeText = cells.get(typeIndex).getText().trim();
            Assert.assertTrue("Each row should have type DAB", typeText.contains("ДАБ"));
        }
    }

    private int findColumnIndex(WebElement table, String columnTitle) {
        List<WebElement> headers = table.findElements(By.cssSelector("thead th"));
        for (int i = 0; i < headers.size(); i++) {
            String headerText = headers.get(i).getText();
            if (headerText != null && headerText.contains(columnTitle)) {
                return i;
            }
        }
        return -1;
    }

    private List<WebElement> getBodyRows(WebElement table) {
        List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
        List<WebElement> filtered = new ArrayList<>();
        for (WebElement row : rows) {
            if (!row.findElements(By.cssSelector("td")).isEmpty()) {
                filtered.add(row);
            }
        }
        return filtered;
    }

    private boolean isFilterActive(String columnTitle) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT);
        WebElement trigger = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//th[.//span[contains(text(),'" + columnTitle + "')]]//button")));
        String classes = trigger.getAttribute("class");
        return classes != null && classes.contains("ant-btn-color-blue");
    }

    private boolean isEmptyStateVisible() {
        return !driver.findElements(EMPTY_STATE_LOCATOR).isEmpty()
                || !driver.findElements(EMPTY_TEXT_LOCATOR).isEmpty();
    }
}

