package cn.edu.softarch.assignment2.logging;

import cn.edu.softarch.assignment2.domain.ConversationTurn;
import cn.edu.softarch.assignment2.domain.WorkflowResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes the workflow transcript in machine-readable and human-readable forms.
 */
public class ConversationLogWriter {
    private final Path outputDirectory;
    private final ObjectMapper objectMapper;

    public ConversationLogWriter(Path outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.objectMapper = new ObjectMapper()
                // Keep Instant values as ISO-8601 text so the log is easy to inspect.
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void write(WorkflowResult result) {
        try {
            Files.createDirectories(outputDirectory);
            Files.writeString(outputDirectory.resolve("conversation-log.jsonl"), toJsonl(result), StandardCharsets.UTF_8);
            Files.writeString(outputDirectory.resolve("conversation-log.md"), toMarkdown(result), StandardCharsets.UTF_8);
            Files.writeString(outputDirectory.resolve("report-draft.md"), result.finalReportDraft(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write logs to " + outputDirectory, e);
        }
    }

    private String toJsonl(WorkflowResult result) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (ConversationTurn turn : result.turns()) {
            builder.append(objectMapper.writeValueAsString(turn)).append('\n');
        }
        return builder.toString();
    }

    private String toMarkdown(WorkflowResult result) {
        StringBuilder builder = new StringBuilder("# Complete Conversation Log\n\n");
        for (ConversationTurn turn : result.turns()) {
            builder.append("## ")
                    .append(turn.timestamp())
                    .append(" | Iteration ")
                    .append(turn.iteration())
                    .append(" | ")
                    .append(turn.addStep())
                    .append(" | ")
                    .append(turn.agentRole())
                    .append("\n\n")
                    .append("### Prompt\n\n")
                    .append(turn.prompt())
                    .append("\n\n### Response\n\n")
                    .append(turn.response())
                    .append("\n\n");
        }
        return builder.toString();
    }
}
