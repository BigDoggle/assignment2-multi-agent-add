package cn.edu.softarch.assignment2.cli;

import cn.edu.softarch.assignment2.agent.AgentPromptFactory;
import cn.edu.softarch.assignment2.config.AssignmentProperties;
import cn.edu.softarch.assignment2.knowledge.KnowledgeBase;
import cn.edu.softarch.assignment2.llm.ChatGateway;
import cn.edu.softarch.assignment2.logging.ConversationLogWriter;
import cn.edu.softarch.assignment2.workflow.AddWorkflow;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.nio.file.Path;

@ShellComponent
public class RunAssignmentCommand {
    private final ChatGateway chatGateway;
    private final AssignmentProperties properties;

    public RunAssignmentCommand(ChatGateway chatGateway, AssignmentProperties properties) {
        this.chatGateway = chatGateway;
        this.properties = properties;
    }

    /**
     * Runs the complete assignment workflow and writes all generated artifacts to disk.
     */
    @ShellMethod(key = "run-assignment", value = "Run the multi-agent ADD workflow for Assignment 2.")
    public String runAssignment(
            @ShellOption(
                    value = "--output-dir",
                    defaultValue = ShellOption.NULL,
                    help = "Output directory for logs and report draft."
            )
            String outputDir,
            @ShellOption(
                    value = "--max-turns",
                    defaultValue = "0",
                    help = "Maximum agent turns to run. Use 0 for the full assignment workflow."
            )
            int maxTurns
    ) {
        String resolvedOutputDir = resolveOutputDirectory(outputDir);
        var knowledgeBase = KnowledgeBase.fromClasspath();
        var promptFactory = new AgentPromptFactory(knowledgeBase);
        var workflow = new AddWorkflow(promptFactory, chatGateway);

        var result = workflow.run(maxTurns);
        new ConversationLogWriter(Path.of(resolvedOutputDir)).write(result);

        return "Assignment run complete. Outputs written to " + resolvedOutputDir;
    }

    private String resolveOutputDirectory(String outputDir) {
        if (outputDir == null || outputDir.isBlank()) {
            return properties.outputDir();
        }
        return outputDir;
    }
}
