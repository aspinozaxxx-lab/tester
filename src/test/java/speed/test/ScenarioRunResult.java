package speed.test;

import java.util.Map;

/**
 * Dannye ob odnom zapuske scenarija dlja nagruzki.
 */
public class ScenarioRunResult {
    private final ScenarioType scenarioType;
    private final int instanceId; // nomer ekzempljara vnutri odnoj gruppy (1..N)
    private final boolean success;
    private final long totalTimeMs;
    private final Map<String, Long> checkpoints; // nazvanie chekpointa -> vremja v ms
    private final String errorMessage;
    private final String errorStackTrace;

    public ScenarioRunResult(ScenarioType scenarioType,
                             int instanceId,
                             boolean success,
                             long totalTimeMs,
                             Map<String, Long> checkpoints,
                             String errorMessage,
                             String errorStackTrace) {
        this.scenarioType = scenarioType;
        this.instanceId = instanceId;
        this.success = success;
        this.totalTimeMs = totalTimeMs;
        this.checkpoints = checkpoints;
        this.errorMessage = errorMessage;
        this.errorStackTrace = errorStackTrace;
    }

    public ScenarioType getScenarioType() {
        return scenarioType;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getTotalTimeMs() {
        return totalTimeMs;
    }

    public Map<String, Long> getCheckpoints() {
        return checkpoints;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorStackTrace() {
        return errorStackTrace;
    }
}
