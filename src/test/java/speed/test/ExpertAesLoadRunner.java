package speed.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Nagruzchnyj raner, kotoryj beret plan iz faila i vyzyvaet UI scenarii.
 */
public class ExpertAesLoadRunner {

    private static final String BASE_URL = BaseUiTest.BASE_URL;
    private static final Duration IMPLICIT_WAIT = Duration.ofSeconds(5);
    private static final LoadTestLogger LOGGER = new LoadTestLogger();
    private static final String PROCESS_FILTER_VALUE = "��������� ��ᮮ⢥��⢨� ���㤭����� ���";
    private static final String STAGE_FILTER_VALUE = "��ଫ���� ��ᮮ⢥��⢨�";

    @Test
    public void runLoadPlan() throws Exception {
        List<String> planLines = readPlanLines();
        for (String rawLine : planLines) {
            if (rawLine == null) {
                continue;
            }
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Nevernyj format stroki plana: " + line);
            }
            ScenarioType scenarioType = ScenarioType.valueOf(parts[0].trim());
            int instancesCount = Integer.parseInt(parts[1].trim());
            executeScenarioGroup(scenarioType, instancesCount);
        }
    }

    private void executeScenarioGroup(ScenarioType scenarioType, int instancesCount) throws InterruptedException {
        LOGGER.logScenarioHeader(scenarioType, instancesCount);
        List<ScenarioRunResult> results = Collections.synchronizedList(new ArrayList<>());
        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= instancesCount; i++) {
            final int instanceId = i;
            Thread thread = new Thread(() -> {
                ScenarioRunResult result = runScenario(scenarioType, instanceId);
                results.add(result);
            }, scenarioType.name() + "-instance-" + instanceId);
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        List<ScenarioRunResult> orderedResults;
        synchronized (results) {
            orderedResults = new ArrayList<>(results);
        }
        orderedResults.sort(Comparator.comparingInt(ScenarioRunResult::getInstanceId));
        int okCount = 0;
        int errorCount = 0;
        long totalTimeMs = 0L;
        for (ScenarioRunResult result : orderedResults) {
            if (result.isSuccess()) {
                okCount++;
            } else {
                errorCount++;
            }
            totalTimeMs = Math.max(totalTimeMs, result.getTotalTimeMs());
            LOGGER.logScenarioInstanceResult(result);
        }
        LOGGER.logScenarioSummary(scenarioType, instancesCount, okCount, errorCount, totalTimeMs);
    }

    private List<String> readPlanLines() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("load-plan.txt")) {
            if (inputStream == null) {
                throw new IllegalStateException("Fail load-plan.txt ne naiden v classpath");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                return lines;
            }
        }
    }

    private ScenarioRunResult runScenario(ScenarioType scenarioType, int instanceId) {
        WebDriver driver = null;
        Map<String, Long> checkpoints = new LinkedHashMap<>();
        boolean success = false;
        String errorMessage = null;
        String errorStackTrace = null;
        long startTotal = System.nanoTime();
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT);
            driver.get(BASE_URL);
            switch (scenarioType) {
                case EXPORT_FNPP:
                    runExportFnppScenario(driver, checkpoints);
                    break;
                case MYTASKS_FILTER:
                    runMyTasksFilterScenario(driver, checkpoints);
                    break;
                case TDREG_FILTER:
                    runTdRegFilterScenario(driver, checkpoints);
                    break;
                default:
                    throw new IllegalArgumentException("Neizvestnyj scenarij: " + scenarioType);
            }
            success = true;
        } catch (Exception | AssertionError e) {
            errorMessage = e.getMessage();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            errorStackTrace = sw.toString();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        long totalTimeMs = (System.nanoTime() - startTotal) / 1_000_000;
        Map<String, Long> checkpointsCopy = new LinkedHashMap<>(checkpoints);
        return new ScenarioRunResult(
                scenarioType,
                instanceId,
                success,
                totalTimeMs,
                checkpointsCopy,
                errorMessage,
                errorStackTrace
        );
    }

    private void runExportFnppScenario(WebDriver driver, Map<String, Long> checkpoints) {
        long checkpointStart = System.nanoTime();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAsExpertAes();
        checkpoints.put("LOGIN", elapsedMillis(checkpointStart));

        Sidebar sidebar = new Sidebar(driver);
        checkpointStart = System.nanoTime();
        sidebar.openAnalysisFnppRegistry();
        checkpoints.put("OPEN_ANALYSIS_REGISTRY", elapsedMillis(checkpointStart));

        AnalysisFnppPage analysisPage = new AnalysisFnppPage(driver);
        checkpointStart = System.nanoTime();
        analysisPage.openFirstAnalysisCard();
        checkpoints.put("OPEN_CARD", elapsedMillis(checkpointStart));

        checkpointStart = System.nanoTime();
        analysisPage.openExportDialog();
        analysisPage.selectInternalTableCheckbox();
        analysisPage.selectXlsxFormat();
        analysisPage.exportReport();
        checkpoints.put("EXPORT_REPORT", elapsedMillis(checkpointStart));
    }

    private void runMyTasksFilterScenario(WebDriver driver, Map<String, Long> checkpoints) {
        long checkpointStart = System.nanoTime();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAsExpertAes();
        checkpoints.put("LOGIN", elapsedMillis(checkpointStart));

        Sidebar sidebar = new Sidebar(driver);
        checkpointStart = System.nanoTime();
        sidebar.openMyTasks();
        checkpoints.put("OPEN_MY_TASKS", elapsedMillis(checkpointStart));

        MyTasksPage tasksPage = new MyTasksPage(driver);
        checkpointStart = System.nanoTime();
        tasksPage.openIncomingTab();
        checkpoints.put("OPEN_INCOMING", elapsedMillis(checkpointStart));

        checkpointStart = System.nanoTime();
        tasksPage.setProcessFilter(PROCESS_FILTER_VALUE);
        tasksPage.setStageFilter(STAGE_FILTER_VALUE);
        tasksPage.applyFilter();
        checkpoints.put("APPLY_FILTERS", elapsedMillis(checkpointStart));
    }

    private void runTdRegFilterScenario(WebDriver driver, Map<String, Long> checkpoints) {
        long checkpointStart = System.nanoTime();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAsExpertAes();
        checkpoints.put("LOGIN", elapsedMillis(checkpointStart));

        Sidebar sidebar = new Sidebar(driver);
        checkpointStart = System.nanoTime();
        sidebar.openTdRegistry();
        checkpoints.put("OPEN_TD_REGISTRY", elapsedMillis(checkpointStart));

        TdRegistryPage tdRegistryPage = new TdRegistryPage(driver);
        checkpointStart = System.nanoTime();
        tdRegistryPage.setTypeFilterDab();
        tdRegistryPage.applyFilter();
        checkpoints.put("APPLY_FILTER_DAB", elapsedMillis(checkpointStart));
    }

    private long elapsedMillis(long startNano) {
        return (System.nanoTime() - startNano) / 1_000_000;
    }
}

