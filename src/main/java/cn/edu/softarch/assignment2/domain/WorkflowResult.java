package cn.edu.softarch.assignment2.domain;

import java.util.List;

/**
 * Stores the complete workflow transcript and the generated report draft.
 */
public record WorkflowResult(List<ConversationTurn> turns, String finalReportDraft) {
    public WorkflowResult {
        turns = List.copyOf(turns);
    }
}
