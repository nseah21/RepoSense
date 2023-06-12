package reposense.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holds the data of set of repos that failed to analyze and the reasons for the failed operation.
 */
public class ErrorSummary {
    private Set<Map<String, String>> errorSet;

    public ErrorSummary() {
        errorSet = new HashSet<>();
    }

    /**
     * Adds an error message for {@code repoName} with the reason {@code errorMessage} into a set of errors.
     */
    public void addErrorMessage(String repoName, String errorMessage) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("repoName", repoName);
        errorDetails.put("errorMessage", errorMessage);
        errorSet.add(errorDetails);
    }

    /**
     * Returns a compiled set of repos that failed to analyze and the corresponding reasons.
     */
    public Set<Map<String, String>> getErrorSet() {
        return errorSet;
    }

    /**
     * Clears all previously stored set of errors in {@link ErrorSummary#errorSet}.
     */
    public void clearErrorSet() {
        errorSet.clear();
    }
}
