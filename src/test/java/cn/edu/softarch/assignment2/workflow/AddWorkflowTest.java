package cn.edu.softarch.assignment2.workflow;

import cn.edu.softarch.assignment2.agent.AgentPromptFactory;
import cn.edu.softarch.assignment2.domain.AgentRole;
import cn.edu.softarch.assignment2.domain.ConversationTurn;
import cn.edu.softarch.assignment2.domain.WorkflowResult;
import cn.edu.softarch.assignment2.knowledge.KnowledgeBase;
import cn.edu.softarch.assignment2.llm.FakeChatGateway;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddWorkflowTest {
    @Test
    void runsFourIterationsWithFiveAgentsPerAddStep() {
        AddWorkflow workflow = new AddWorkflow(
                new AgentPromptFactory(KnowledgeBase.fromClasspath()),
                new FakeChatGateway()
        );

        WorkflowResult result = workflow.run();

        assertThat(result.turns()).hasSize(4 * 6 * 5);
        assertThat(result.turns()).extracting(ConversationTurn::agentRole)
                .contains(
                        AgentRole.COORDINATOR,
                        AgentRole.ARCHITECT,
                        AgentRole.QUALITY_ANALYST,
                        AgentRole.REVIEWER,
                        AgentRole.RECORDER
                );
        assertThat(result.finalReportDraft()).contains("Iteration 1");
        assertThat(result.finalReportDraft()).contains("Iteration 4");
    }
}
