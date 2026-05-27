package cn.edu.softarch.assignment2.cli;

import cn.edu.softarch.assignment2.llm.ChatGateway;
import cn.edu.softarch.assignment2.llm.FakeChatGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.shell.command.CommandCatalog;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.ai.dashscope.api-key=test-key")
class RunAssignmentCommandTest {
    @Autowired
    RunAssignmentCommand command;

    @Autowired
    CommandCatalog commandCatalog;

    @TempDir
    Path tempDir;

    @Test
    void runAssignmentCommandReturnsOutputDirectory() throws Exception {
        Path outputDirectory = tempDir.resolve("assignment-output");

        String message = command.runAssignment(outputDirectory.toString(), 1);

        assertThat(commandCatalog.getRegistrations()).containsKey("run-assignment");
        assertThat(message).contains(outputDirectory.toString());
        assertThat(Files.exists(outputDirectory.resolve("conversation-log.md"))).isTrue();
    }

    @TestConfiguration
    static class FakeGatewayConfig {
        @Bean
        @Primary
        ChatGateway fakeChatGateway() {
            return new FakeChatGateway();
        }
    }
}
