package cn.edu.softarch.assignment2.agent;

import cn.edu.softarch.assignment2.domain.AddStep;
import cn.edu.softarch.assignment2.domain.AgentRole;
import cn.edu.softarch.assignment2.domain.IterationPlan;
import cn.edu.softarch.assignment2.knowledge.KnowledgeBase;

public class AgentPromptFactory {
    private final KnowledgeBase knowledgeBase;

    public AgentPromptFactory(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    /**
     * Builds one complete prompt for a specific agent, ADD iteration, and ADD step.
     */
    public String promptFor(AgentRole role, IterationPlan iteration, AddStep step, String previousContext) {
        return """
                %s

                # Current Agent
                Role: %s

                # Role Responsibilities
                %s

                # Current ADD Work
                Iteration %d: %s
                ADD %s

                # Previous Context
                %s

                # Required Output Format
                1. Drivers considered in this step.
                2. Reasoning derived only from provided knowledge.
                3. Decisions made by this agent.
                4. Mermaid or PlantUML view code when the step creates or updates a view.
                5. Self-check: state whether external knowledge was avoided.

                Do not introduce external domain knowledge.
                """.formatted(
                knowledgeBase.combined(),
                role.name(),
                responsibilities(role),
                iteration.number(),
                iteration.title(),
                stepLabel(step),
                previousContext
        );
    }

    private String responsibilities(AgentRole role) {
        return switch (role) {
            case COORDINATOR -> "Control the multi-agent workflow and keep the response aligned with the selected ADD step.";
            case ARCHITECT -> "Propose architectural structures, responsibilities, interfaces, and views for the Hotel Pricing System.";
            case QUALITY_ANALYST -> "Check performance, reliability, availability, scalability, security, modifiability, deployability, monitorability, and testability drivers from the assignment.";
            case REVIEWER -> "Verify that outputs follow ADD 3.0, the iteration goal, and the no-external-knowledge rule.";
            case RECORDER -> "Summarize accepted decisions and format them for the report template.";
        };
    }

    private String stepLabel(AddStep step) {
        return switch (step) {
            case STEP_1_REVIEW_INPUTS -> "Step 1: Review Inputs";
            case STEP_2_SELECT_DRIVERS -> "Step 2: Establish the Iteration Goal by Selecting Drivers";
            case STEP_3_SELECT_ELEMENTS -> "Step 3: Choose One or More Elements of the System to Refine";
            case STEP_4_SELECT_DESIGN_CONCEPTS -> "Step 4: Choose One or More Design Concepts That Satisfy the Selected Drivers";
            case STEP_5_INSTANTIATE_ELEMENTS -> "Step 5: Instantiate Architectural Elements, Allocate Responsibilities, and Define Interfaces";
            case STEP_6_SKETCH_VIEWS_AND_RECORD_DECISIONS -> "Step 6: Sketch Views and Record Design Decisions";
            case STEP_7_ANALYZE_DESIGN -> "Step 7: Perform Analysis of Current Design and Review Iteration Goal";
        };
    }
}
