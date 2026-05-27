package cn.edu.softarch.assignment2.logging;

import cn.edu.softarch.assignment2.domain.AddStep;
import cn.edu.softarch.assignment2.domain.AgentRole;
import cn.edu.softarch.assignment2.domain.ConversationTurn;
import cn.edu.softarch.assignment2.domain.WorkflowResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConversationLogWriterTest {
    @TempDir
    Path tempDir;

    @Test
    void writesJsonlAndMarkdownLogsWithTimestamp() throws Exception {
        WorkflowResult result = new WorkflowResult(List.of(
                new ConversationTurn(Instant.parse("2026-05-27T10:00:00Z"), 1,
                        AddStep.STEP_2_SELECT_DRIVERS, AgentRole.ARCHITECT, "prompt", "response")
        ), "# report");

        ConversationLogWriter writer = new ConversationLogWriter(tempDir);
        writer.write(result);

        String jsonl = Files.readString(tempDir.resolve("conversation-log.jsonl"));
        String markdown = Files.readString(tempDir.resolve("conversation-log.md"));
        String report = Files.readString(tempDir.resolve("report-draft.md"));

        assertThat(jsonl).contains("2026-05-27T10:00:00Z");
        assertThat(markdown).contains("ARCHITECT");
        assertThat(report).contains("# report");
    }
}
