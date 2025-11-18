package speed.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Logger dlja vygruzki rezultatov nagruzki v fajly.
 */
public class LoadTestLogger {
    private static final Path RESULTS_DIR = Paths.get("target", "load-results");
    private static final Path REPORT_FILE = RESULTS_DIR.resolve("load-report.txt");
    private static final Path ERROR_FILE = RESULTS_DIR.resolve("load-errors.txt");

    static {
        try {
            Files.createDirectories(RESULTS_DIR);
            resetLogFile(REPORT_FILE);
            resetLogFile(ERROR_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Ne udalos sozdati direktoriju load-results", e);
        }
    }

    public synchronized void logScenarioHeader(ScenarioType scenarioType, int instancesCount) {
        String line = String.format("=== %s x%d ===%n%n", scenarioType.name(), instancesCount);
        appendLine(REPORT_FILE, line);
    }

    public synchronized void logScenarioInstanceResult(ScenarioRunResult result) {
        String checkpointsText = formatCheckpoints(result.getCheckpoints());
        String status = result.isSuccess() ? "OK" : "ERROR";
        String line = String.format(
                "#%d %s total=%dms %s%n",
                result.getInstanceId(),
                status,
                result.getTotalTimeMs(),
                checkpointsText
        );
        appendLine(REPORT_FILE, line);
        if (!result.isSuccess()) {
            logErrorDetails(result);
        }
    }

    public synchronized void logScenarioSummary(ScenarioType scenarioType,
                                                int instancesCount,
                                                int okCount,
                                                int errorCount,
                                                long totalTimeMs) {
        String summary = String.format(
                "Itog po %s x%d: ok=%d, error=%d, totalTime=%dms%n",
                scenarioType.name(),
                instancesCount,
                okCount,
                errorCount,
                totalTimeMs
        );
        String divider = "----------------------------------------";
        appendLine(REPORT_FILE, summary);
        appendLine(REPORT_FILE, divider + System.lineSeparator());
    }

    private void logErrorDetails(ScenarioRunResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("=== %s instance #%d ===%n", result.getScenarioType().name(), result.getInstanceId()));
        builder.append("status=ERROR").append(System.lineSeparator());
        builder.append("error=").append(result.getErrorMessage() == null ? "" : result.getErrorMessage()).append(System.lineSeparator());
        builder.append("stacktrace:").append(System.lineSeparator());
        if (result.getErrorStackTrace() != null) {
            builder.append(result.getErrorStackTrace());
        }
        if (!builder.toString().endsWith(System.lineSeparator())) {
            builder.append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
        appendLine(ERROR_FILE, builder.toString());
    }

    private String formatCheckpoints(Map<String, Long> checkpoints) {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        if (checkpoints != null) {
            for (Map.Entry<String, Long> entry : checkpoints.entrySet()) {
                joiner.add(entry.getKey() + "=" + entry.getValue() + "ms");
            }
        }
        return joiner.toString();
    }

    private void appendLine(Path file, String text) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                file,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            writer.write(text);
        } catch (IOException e) {
            throw new RuntimeException("Oshibka zapisi v " + file, e);
        }
    }

    private static void resetLogFile(Path file) throws IOException {
        // fajly otcheta peresozdajutsja pered novym progonom
        try (BufferedWriter writer = Files.newBufferedWriter(
                file,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            // pusto, tolko ochistka
        }
    }
}
