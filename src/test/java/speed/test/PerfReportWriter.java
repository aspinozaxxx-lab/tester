package speed.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pishet otdelnyj perf-otchet po rezultatam nagruzki.
 */
public final class PerfReportWriter {

    private static final Path RESULTS_DIR = Paths.get("target", "load-results");
    private static final DateTimeFormatter TS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter FILE_TS_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS");

    private PerfReportWriter() {
    }

    public static void writePerfReport(ScenarioType scenarioType,
                                       List<ScenarioRunResult> results,
                                       LocalDateTime startTime,
                                       LocalDateTime endTime) {
        if (results == null || results.isEmpty()) {
            return;
        }

        String testTitle = resolveTitle(scenarioType);
        int runsCount = results == null ? 0 : results.size();
        double avgScenarioTimeMs = results.stream()
                .mapToLong(ScenarioRunResult::getTotalTimeMs)
                .average()
                .orElse(0D);

        // Poryadok chekpointov s okhranenijem ocherednosti pervoj vstrechi
        Map<String, List<Long>> checkpointValues = new LinkedHashMap<>();
        for (ScenarioRunResult result : results) {
            Map<String, Long> checkpoints = result.getCheckpoints();
            if (checkpoints == null) {
                continue;
            }
            for (Map.Entry<String, Long> entry : checkpoints.entrySet()) {
                checkpointValues
                        .computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                        .add(entry.getValue());
            }
        }

        String fileName = String.format(
                "perf-%s-%s.txt",
                scenarioType.name(),
                FILE_TS_FORMATTER.format(LocalDateTime.now())
        );
        Path reportFile = RESULTS_DIR.resolve(fileName);

        List<String> lines = new ArrayList<>();
        lines.add("Название теста: " + testTitle);
        lines.add("Количество проходов: " + runsCount);
        lines.add("Время начала выполнения: " + TS_FORMATTER.format(startTime));
        lines.add("Время завершения выполнениня: " + TS_FORMATTER.format(endTime));
        lines.add(String.format("Среднее время выполнения задачи: %.0f мс", avgScenarioTimeMs));
        lines.add("Время выполнения задач (мин / среднее / макс):");

        for (Map.Entry<String, List<Long>> entry : checkpointValues.entrySet()) {
            List<Long> values = entry.getValue();
            if (values == null || values.isEmpty()) {
                continue;
            }
            long min = values.stream().mapToLong(Long::longValue).min().orElse(0L);
            double avg = values.stream().mapToLong(Long::longValue).average().orElse(0D);
            long max = values.stream().mapToLong(Long::longValue).max().orElse(0L);
            lines.add(String.format("    %s: %d / %.0f / %d ms", entry.getKey(), min, avg, max));
        }

        try {
            Files.createDirectories(RESULTS_DIR);
            try (BufferedWriter writer = Files.newBufferedWriter(
                    reportFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ne udalos zapisat perf-otchet " + reportFile, e);
        }
    }

    private static String resolveTitle(ScenarioType scenarioType) {
        // chelovekochitaemye nazvaniya dlya otcheta
        return switch (scenarioType) {
            case EXPORT_FNPP -> "Выгрузка отчета";
            case MYTASKS_FILTER -> "Мои задачи - фильтры процесс и название этапа";
            case TDREG_FILTER -> "Реестр ТД - фильтр вид";
            default -> scenarioType.name();
        };
    }
}


