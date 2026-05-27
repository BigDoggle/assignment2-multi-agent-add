package cn.edu.softarch.assignment2.agent;

import cn.edu.softarch.assignment2.domain.AddStep;
import cn.edu.softarch.assignment2.domain.AgentRole;
import cn.edu.softarch.assignment2.domain.IterationPlan;
import cn.edu.softarch.assignment2.knowledge.KnowledgeBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgentPromptFactoryTest {
    @Test
    void architectPromptContainsNoExternalKnowledgeRuleAndMermaidRequirement() {
        AgentPromptFactory factory = new AgentPromptFactory(KnowledgeBase.fromClasspath());

        String prompt = factory.promptFor(
                AgentRole.ARCHITECT,
                IterationPlan.assignmentDefault().get(0),
                AddStep.STEP_4_SELECT_DESIGN_CONCEPTS,
                "No previous result yet."
        );

        assertThat(prompt).contains("Do not introduce external domain knowledge");
        assertThat(prompt).contains("Mermaid or PlantUML");
        assertThat(prompt).contains("ARCHITECT");
        assertThat(prompt).contains("Step 4");
    }
}
