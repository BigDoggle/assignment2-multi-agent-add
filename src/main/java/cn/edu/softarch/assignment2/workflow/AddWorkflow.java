package cn.edu.softarch.assignment2.workflow;

import cn.edu.softarch.assignment2.agent.AgentPromptFactory;
import cn.edu.softarch.assignment2.domain.AgentRole;
import cn.edu.softarch.assignment2.domain.ConversationTurn;
import cn.edu.softarch.assignment2.domain.IterationPlan;
import cn.edu.softarch.assignment2.domain.WorkflowResult;
import cn.edu.softarch.assignment2.llm.ChatGateway;
import cn.edu.softarch.assignment2.llm.ChatRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AddWorkflow {
    private static final List<AgentRole> AGENT_ORDER = List.of(
            AgentRole.COORDINATOR,
            AgentRole.ARCHITECT,
            AgentRole.QUALITY_ANALYST,
            AgentRole.REVIEWER,
            AgentRole.RECORDER
    );

    private final AgentPromptFactory promptFactory;
    private final ChatGateway chatGateway;

    public AddWorkflow(AgentPromptFactory promptFactory, ChatGateway chatGateway) {
        this.promptFactory = promptFactory;
        this.chatGateway = chatGateway;
    }

    /**
     * Runs the deterministic four-iteration ADD workflow and records every agent turn.
     */
    public WorkflowResult run() {
        return run(0);
    }

    public WorkflowResult run(int maxTurns) {
        List<ConversationTurn> turns = new ArrayList<>();
        String context = "ADD Step 1 reviewed all provided inputs: ADD method, Hotel Pricing System case, "
                + "quality attributes, concerns, constraints, and selected multi-agent setup.";
        StringBuilder report = new StringBuilder("# ADD Output Results\n\nADD Step 1:\n")
                .append(context)
                .append("\n\n");

        for (IterationPlan iteration : IterationPlan.assignmentDefault()) {
            report.append("## Iteration ")
                    .append(iteration.number())
                    .append(": ")
                    .append(iteration.title())
                    .append("\n\n");

            for (var step : iteration.steps()) {
                report.append("### ").append(step.name()).append("\n\n");
                for (AgentRole role : AGENT_ORDER) {
                    if (maxTurns > 0 && turns.size() >= maxTurns) {
                        report.append("> Workflow stopped after ")
                                .append(maxTurns)
                                .append(" turns for a smoke run.\n\n");
                        return new WorkflowResult(List.copyOf(turns), report.toString());
                    }
                    String prompt = promptFactory.promptFor(role, iteration, step, context);
                    System.out.printf(
                            "Calling %s for iteration %d %s (%d/%s)%n",
                            role,
                            iteration.number(),
                            step,
                            turns.size() + 1,
                            maxTurns > 0 ? maxTurns : "120"
                    );
                    var response = chatGateway.chat(new ChatRequest(prompt));
                    System.out.printf(
                            "Completed %s for iteration %d %s%n",
                            role,
                            iteration.number(),
                            step
                    );
                    ConversationTurn turn = new ConversationTurn(
                            Instant.now(),
                            iteration.number(),
                            step,
                            role,
                            prompt,
                            response.content()
                    );
                    turns.add(turn);
                    context = response.content();
                    report.append("**")
                            .append(role.name())
                            .append("**\n\n")
                            .append(response.content())
                            .append("\n\n");
                }
            }
        }

        return new WorkflowResult(List.copyOf(turns), report.toString());
    }
}
