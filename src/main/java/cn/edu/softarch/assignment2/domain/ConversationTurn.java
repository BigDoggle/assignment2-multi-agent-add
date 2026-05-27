package cn.edu.softarch.assignment2.domain;

import java.time.Instant;

/**
 * Captures one timestamped agent exchange inside the ADD workflow.
 */
public record ConversationTurn(
        Instant timestamp,
        int iteration,
        AddStep addStep,
        AgentRole agentRole,
        String prompt,
        String response
) {
}
