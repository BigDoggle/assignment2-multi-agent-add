# Multi-Agent ADD Source Code Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the assignment source code: a Spring AI Alibaba + Qwen3-Max multi-agent program that runs ADD 3.0 for the Hotel Pricing System and records timestamped four-iteration conversation logs.

**Architecture:** Create a small Spring Boot Maven application with explicit domain models for ADD inputs, agent roles, messages, workflow steps, and run results. Keep the orchestration deterministic and testable by using a `ChatGateway` interface with a fake implementation in tests and a DashScope/Qwen implementation for real runs. The runtime will execute four ADD iterations through planner, architect, quality, reviewer, and recorder agents, then write JSONL and Markdown logs.

**Tech Stack:** Java 17, Maven, Spring Boot 3.5.x, Spring AI Alibaba DashScope starter, Qwen3-Max, JUnit 5, AssertJ, Jackson, Spring Shell.

---

## File Structure

- Create `pom.xml`: Maven project, dependency versions, Spring Boot plugin, test dependencies.
- Create `README.md`: setup, environment variables, commands, assignment mapping.
- Create `.gitignore`: Java/Maven/runtime output ignores.
- Create `src/main/resources/application.yml`: Qwen3-Max model config and output directory.
- Create `src/main/resources/knowledge/add-3.0.md`: assignment-provided ADD 3.0 method only.
- Create `src/main/resources/knowledge/hotel-pricing-system.md`: assignment-provided HPS case only.
- Create `src/main/resources/prompts/system-policy.md`: shared constraints: no external knowledge, no few-shot, Mermaid/PlantUML views, explicit decisions.
- Create `src/main/java/cn/edu/softarch/assignment2/Assignment2Application.java`: Spring Boot entry point.
- Create `src/main/java/cn/edu/softarch/assignment2/config/AssignmentProperties.java`: strongly typed runtime config.
- Create `src/main/java/cn/edu/softarch/assignment2/config/QwenConfig.java`: real `ChatGateway` bean using Spring AI Alibaba.
- Create `src/main/java/cn/edu/softarch/assignment2/domain/*.java`: immutable records for agents, iterations, messages, decisions, logs.
- Create `src/main/java/cn/edu/softarch/assignment2/knowledge/KnowledgeBase.java`: loads the three Markdown knowledge/policy files.
- Create `src/main/java/cn/edu/softarch/assignment2/agent/*.java`: agent interfaces and prompt builders.
- Create `src/main/java/cn/edu/softarch/assignment2/llm/*.java`: `ChatGateway`, request/response records, Qwen adapter.
- Create `src/main/java/cn/edu/softarch/assignment2/workflow/AddWorkflow.java`: executes four ADD iterations with multi-agent collaboration.
- Create `src/main/java/cn/edu/softarch/assignment2/logging/ConversationLogWriter.java`: writes timestamped `.jsonl` and `.md` logs.
- Create `src/main/java/cn/edu/softarch/assignment2/cli/RunAssignmentCommand.java`: command to run the assignment from terminal.
- Create `src/test/java/cn/edu/softarch/assignment2/**`: unit tests for knowledge loading, prompt constraints, workflow order, and log output.

## External References Checked

- Spring AI Alibaba quick start says Java projects can use `spring-ai-alibaba-starter-dashscope`: https://java2ai.com/docs/quick-start
- Spring AI Alibaba GitHub describes it as an agentic AI framework for Java developers and points to DashScope examples: https://github.com/alibaba/spring-ai-alibaba
- Maven repository search showed `com.alibaba.cloud.ai:spring-ai-alibaba` recent line `1.1.2.3` on 2026-05-12. Use this in the plan, then verify with `mvn test` during implementation.

---

### Task 1: Maven Project Skeleton

**Files:**
- Create: `pom.xml`
- Create: `.gitignore`
- Create: `src/main/java/cn/edu/softarch/assignment2/Assignment2Application.java`
- Create: `src/test/java/cn/edu/softarch/assignment2/Assignment2ApplicationTests.java`

- [ ] **Step 1: Create the Maven `pom.xml`**

Use this exact project shape. Keep Java 17 because it is stable for Spring Boot 3.x and easy for student machines.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.13</version>
        <relativePath/>
    </parent>

    <groupId>cn.edu.softarch</groupId>
    <artifactId>assignment2-multi-agent-add</artifactId>
    <version>0.1.0</version>
    <name>assignment2-multi-agent-add</name>
    <description>Multi-agent ADD 3.0 workflow for Software Architecture Assignment 2</description>

    <properties>
        <java.version>17</java.version>
        <spring-ai-alibaba.version>1.1.2.3</spring-ai-alibaba.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.alibaba.cloud.ai</groupId>
                <artifactId>spring-ai-alibaba-bom</artifactId>
                <version>${spring-ai-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.shell</groupId>
            <artifactId>spring-shell-starter</artifactId>
            <version>3.3.3</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud.ai</groupId>
            <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Create `.gitignore`**

```gitignore
target/
.idea/
.vscode/
*.iml
.DS_Store
logs/
out/
*.log
```

- [ ] **Step 3: Create the Spring Boot entry point**

```java
package cn.edu.softarch.assignment2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Assignment2Application {
    public static void main(String[] args) {
        SpringApplication.run(Assignment2Application.class, args);
    }
}
```

- [ ] **Step 4: Create a smoke test**

```java
package cn.edu.softarch.assignment2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Assignment2ApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 5: Run the smoke test**

Run:

```bash
mvn test -Dtest=Assignment2ApplicationTests
```

Expected: build succeeds with `BUILD SUCCESS`.

- [ ] **Step 6: Commit**

```bash
git add pom.xml .gitignore src/main/java/cn/edu/softarch/assignment2/Assignment2Application.java src/test/java/cn/edu/softarch/assignment2/Assignment2ApplicationTests.java
git commit -m "chore: scaffold Spring AI Alibaba assignment project"
```

---

### Task 2: Assignment Knowledge Files

**Files:**
- Create: `src/main/resources/knowledge/add-3.0.md`
- Create: `src/main/resources/knowledge/hotel-pricing-system.md`
- Create: `src/main/resources/prompts/system-policy.md`
- Create: `src/main/java/cn/edu/softarch/assignment2/knowledge/KnowledgeBase.java`
- Test: `src/test/java/cn/edu/softarch/assignment2/knowledge/KnowledgeBaseTest.java`

- [ ] **Step 1: Create `add-3.0.md`**

```markdown
# Attribute-Driven Design (ADD) Method

## Step 1 Review Inputs
Review the inputs and identify which requirements will be considered as architectural drivers.

## Step 2 Establish the Iteration Goal by Selecting Drivers
A design round is a series of design iterations. Each iteration focuses on achieving a particular goal, usually satisfying a subset of the drivers.

## Step 3 Choose One or More Elements of the System to Refine
Select elements involved in satisfying specific drivers. For greenfield development, start by establishing the system context and selecting the system itself for refinement by decomposition.

## Step 4 Choose One or More Design Concepts That Satisfy the Selected Drivers
Identify alternatives among design concepts that can achieve the iteration goal, then select one alternative.

## Step 5 Instantiate Architectural Elements, Allocate Responsibilities, and Define Interfaces
Instantiate architectural elements from selected concepts, assign responsibilities, and establish relationships and interfaces.

## Step 6 Sketch Views and Record Design Decisions
Preserve views, record significant design decisions, and record the rationale behind those decisions.

## Step 7 Perform Analysis of Current Design and Review Iteration Goal and Achievement of Design Purpose
Check whether a partial design satisfying the current iteration goal has been created and whether additional iterations are needed.
```

- [ ] **Step 2: Create `hotel-pricing-system.md`**

```markdown
# Hotel Pricing System

## Design Purpose
This is greenfield development that completely replaces an existing system. The design activity makes initial decisions to support construction from scratch.

## Primary Functionality
- HPS-1 Log In: A commercial user or administrator provides credentials. The system validates them against a user identity service. After login, the user can only query and change hotels they are authorized for.
- HPS-2 Change Prices: An authorized user selects a hotel and dates, changes a base rate or fixed rate, simulates changes, then publishes changed prices to the Channel Management System and query APIs.
- HPS-3 Query Prices: A user or external system queries prices for a hotel through UI or query API.
- HPS-4 Manage Hotels: An administrator adds, changes, or modifies hotel information, tax rates, available rates, and room types.
- HPS-5 Manage Rates: An administrator adds, changes, or modifies rates and calculation business rules.
- HPS-6 Manage Users: An administrator changes permissions for a user.

## Quality Attributes
- QA-1 Performance: Publishing all rates and room types after a base rate change must take less than 100 ms. Use case HPS-2. Importance High. Difficulty High.
- QA-2 Reliability: 100% of multiple price changes must be published and received by the Channel Management System. Use case HPS-2. Importance High. Difficulty High.
- QA-3 Availability: Pricing query uptime SLA must be 99.9% outside maintenance windows. Use cases All. Importance High. Difficulty High.
- QA-4 Scalability: Initially support at least 100,000 price queries per day through API and handle up to 1,000,000 without average latency degradation greater than 20%. Use case HPS-3. Importance High. Difficulty High.
- QA-5 Security: Validate front-end login credentials against User Identity Service and show only authorized functions. Use cases All. Importance High. Difficulty Medium.
- QA-6 Modifiability: Add a non-REST price query endpoint such as gRPC without changing core components. Use cases All. Importance Medium. Difficulty Medium.
- QA-7 Deployability: Move the application between nonproduction environments with no code changes. Use cases All. Importance Medium. Difficulty Medium.
- QA-8 Monitorability: Provide a mechanism to collect 100% of needed performance and reliability measures for price publication. Use case HPS-2. Importance Medium. Difficulty Medium.
- QA-9 Testability: 100% of the system and elements support integration testing independently of external systems. Use cases All. Importance Medium. Difficulty Medium.

## Architectural Concerns
- CRN-1 Establish an overall initial system structure.
- CRN-2 Leverage the team's knowledge about Java technologies, Angular, and Kafka.
- CRN-3 Allocate work to members of the development team.
- CRN-4 Avoid introducing technical debt.
- CRN-5 Set up a continuous deployment infrastructure.

## Constraints
- CON-1 Users must interact through a web browser on Windows, OSX, Linux, and different devices.
- CON-2 Manage users through cloud provider identity service and host resources in the cloud.
- CON-3 Code must be hosted on the company's proprietary Git-based platform.
- CON-4 Initial release in 6 months, MVP demo in at most 2 months.
- CON-5 Initial integration with existing systems through REST APIs, later support for other protocols may be needed.
- CON-6 Favor a cloud-native approach.
```

- [ ] **Step 3: Create `system-policy.md`**

```markdown
# System Policy

You are completing Software Architecture Assignment 2 with:
- AI paradigm: multi-agent
- Basic LLM: Qwen3-Max
- Agent framework: Spring AI Alibaba

Rules:
1. Use only the provided ADD 3.0 method, Hotel Pricing System case, and iteration plan.
2. Do not introduce external domain knowledge.
3. Do not use few-shot examples or handcrafted demonstration outputs.
4. Do not reinterpret or augment requirements beyond the provided knowledge.
5. Derive all decision rules from the system instructions and provided assignment knowledge.
6. Agents may decompose tasks, plan reasoning steps, and perform self-verification only when those behaviors are derived from these instructions.
7. Views produced during iterations must be Mermaid or PlantUML code.

Iteration plan:
1. Establishing an Overall System Structure.
2. Identifying Structures to Support Primary Functionality.
3. Addressing Reliability and Availability Quality Attributes.
4. Addressing Development and Operations.
```

- [ ] **Step 4: Write the failing knowledge loader test**

```java
package cn.edu.softarch.assignment2.knowledge;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeBaseTest {
    @Test
    void loadsAssignmentKnowledgeAndPolicy() {
        KnowledgeBase base = KnowledgeBase.fromClasspath();

        assertThat(base.addMethod()).contains("Step 1 Review Inputs");
        assertThat(base.hotelPricingSystem()).contains("QA-3 Availability");
        assertThat(base.systemPolicy()).contains("AI paradigm: multi-agent");
        assertThat(base.combined()).contains("Qwen3-Max");
    }
}
```

- [ ] **Step 5: Run the test and verify it fails**

Run:

```bash
mvn test -Dtest=KnowledgeBaseTest
```

Expected: FAIL because `KnowledgeBase` does not exist.

- [ ] **Step 6: Implement `KnowledgeBase`**

```java
package cn.edu.softarch.assignment2.knowledge;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record KnowledgeBase(String addMethod, String hotelPricingSystem, String systemPolicy) {
    public static KnowledgeBase fromClasspath() {
        return new KnowledgeBase(
                load("/knowledge/add-3.0.md"),
                load("/knowledge/hotel-pricing-system.md"),
                load("/prompts/system-policy.md")
        );
    }

    public String combined() {
        return "# Prior Knowledge\n\n" + addMethod + "\n\n" + hotelPricingSystem + "\n\n" + systemPolicy;
    }

    private static String load(String path) {
        try (var stream = KnowledgeBase.class.getResourceAsStream(path)) {
            Objects.requireNonNull(stream, "Missing classpath resource: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load " + path, e);
        }
    }
}
```

- [ ] **Step 7: Run the test and verify it passes**

Run:

```bash
mvn test -Dtest=KnowledgeBaseTest
```

Expected: PASS.

- [ ] **Step 8: Commit**

```bash
git add src/main/resources src/main/java/cn/edu/softarch/assignment2/knowledge src/test/java/cn/edu/softarch/assignment2/knowledge
git commit -m "feat: add assignment knowledge base"
```

---

### Task 3: Domain Model

**Files:**
- Create: `src/main/java/cn/edu/softarch/assignment2/domain/AgentRole.java`
- Create: `src/main/java/cn/edu/softarch/assignment2/domain/AddStep.java`
- Create: `src/main/java/cn/edu/softarch/assignment2/domain/IterationPlan.java`
- Create: `src/main/java/cn/edu/softarch/assignment2/domain/ChatMessage.java`
- Create: `src/main/java/cn/edu/softarch/assignment2/domain/ConversationTurn.java`
- Create: `src/main/java/cn/edu/softarch/assignment2/domain/WorkflowResult.java`
- Test: `src/test/java/cn/edu/softarch/assignment2/domain/IterationPlanTest.java`

- [ ] **Step 1: Write the failing iteration plan test**

```java
package cn.edu.softarch.assignment2.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IterationPlanTest {
    @Test
    void defaultPlanContainsFourAssignmentIterations() {
        assertThat(IterationPlan.assignmentDefault())
                .extracting(IterationPlan::title)
                .containsExactly(
                        "Establishing an Overall System Structure",
                        "Identifying Structures to Support Primary Functionality",
                        "Addressing Reliability and Availability Quality Attributes",
                        "Addressing Development and Operations"
                );
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn test -Dtest=IterationPlanTest
```

Expected: FAIL because domain records do not exist.

- [ ] **Step 3: Implement domain records and enums**

```java
package cn.edu.softarch.assignment2.domain;

public enum AgentRole {
    COORDINATOR,
    ARCHITECT,
    QUALITY_ANALYST,
    REVIEWER,
    RECORDER
}
```

```java
package cn.edu.softarch.assignment2.domain;

public enum AddStep {
    STEP_1_REVIEW_INPUTS,
    STEP_2_SELECT_DRIVERS,
    STEP_3_SELECT_ELEMENTS,
    STEP_4_SELECT_DESIGN_CONCEPTS,
    STEP_5_INSTANTIATE_ELEMENTS,
    STEP_6_SKETCH_VIEWS_AND_RECORD_DECISIONS,
    STEP_7_ANALYZE_DESIGN
}
```

```java
package cn.edu.softarch.assignment2.domain;

import java.util.List;

public record IterationPlan(int number, String title, List<AddStep> steps) {
    public static List<IterationPlan> assignmentDefault() {
        List<AddStep> iterationSteps = List.of(
                AddStep.STEP_2_SELECT_DRIVERS,
                AddStep.STEP_3_SELECT_ELEMENTS,
                AddStep.STEP_4_SELECT_DESIGN_CONCEPTS,
                AddStep.STEP_5_INSTANTIATE_ELEMENTS,
                AddStep.STEP_6_SKETCH_VIEWS_AND_RECORD_DECISIONS,
                AddStep.STEP_7_ANALYZE_DESIGN
        );
        return List.of(
                new IterationPlan(1, "Establishing an Overall System Structure", iterationSteps),
                new IterationPlan(2, "Identifying Structures to Support Primary Functionality", iterationSteps),
                new IterationPlan(3, "Addressing Reliability and Availability Quality Attributes", iterationSteps),
                new IterationPlan(4, "Addressing Development and Operations", iterationSteps)
        );
    }
}
```

```java
package cn.edu.softarch.assignment2.domain;

public record ChatMessage(String role, String content) {
    public static ChatMessage system(String content) {
        return new ChatMessage("system", content);
    }

    public static ChatMessage user(String content) {
        return new ChatMessage("user", content);
    }

    public static ChatMessage assistant(String content) {
        return new ChatMessage("assistant", content);
    }
}
```

```java
package cn.edu.softarch.assignment2.domain;

import java.time.Instant;

public record ConversationTurn(
        Instant timestamp,
        int iteration,
        AddStep addStep,
        AgentRole agentRole,
        String prompt,
        String response
) {
}
```

```java
package cn.edu.softarch.assignment2.domain;

import java.util.List;

public record WorkflowResult(List<ConversationTurn> turns, String finalReportDraft) {
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
mvn test -Dtest=IterationPlanTest
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/cn/edu/softarch/assignment2/domain src/test/java/cn/edu/softarch/assignment2/domain
git commit -m "feat: model ADD workflow domain"
```

---

### Task 4: Agent Prompt Builders

**Files:**
- Create: `src/main/java/cn/edu/softarch/assignment2/agent/AgentPromptFactory.java`
- Test: `src/test/java/cn/edu/softarch/assignment2/agent/AgentPromptFactoryTest.java`

- [ ] **Step 1: Write failing tests for role prompts and constraints**

```java
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
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn test -Dtest=AgentPromptFactoryTest
```

Expected: FAIL because `AgentPromptFactory` does not exist.

- [ ] **Step 3: Implement `AgentPromptFactory`**

```java
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
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
mvn test -Dtest=AgentPromptFactoryTest
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/cn/edu/softarch/assignment2/agent src/test/java/cn/edu/softarch/assignment2/agent
git commit -m "feat: add multi-agent prompt builder"
```

---

### Task 5: LLM Gateway and Fake Test Adapter

**Files:**
- Create: `src/main/java/cn/edu/softarch/assignment2/llm/ChatGateway.java`
- Create: `src/main/java/cn/edu/softarch/assignment2/llm/ChatRequest.java`
- Create: `src/main/java/cn/edu/softarch/assignment2/llm/ChatResponse.java`
- Create: `src/test/java/cn/edu/softarch/assignment2/llm/FakeChatGateway.java`
- Test: `src/test/java/cn/edu/softarch/assignment2/llm/FakeChatGatewayTest.java`

- [ ] **Step 1: Write failing fake gateway test**

```java
package cn.edu.softarch.assignment2.llm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FakeChatGatewayTest {
    @Test
    void returnsDeterministicResponseContainingAgentPromptSignal() {
        FakeChatGateway gateway = new FakeChatGateway();

        ChatResponse response = gateway.chat(new ChatRequest("ARCHITECT prompt"));

        assertThat(response.content()).contains("Fake response");
        assertThat(response.content()).contains("ARCHITECT prompt");
        assertThat(response.inputTokens()).isGreaterThan(0);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn test -Dtest=FakeChatGatewayTest
```

Expected: FAIL because gateway classes do not exist.

- [ ] **Step 3: Implement gateway records and fake adapter**

```java
package cn.edu.softarch.assignment2.llm;

public interface ChatGateway {
    ChatResponse chat(ChatRequest request);
}
```

```java
package cn.edu.softarch.assignment2.llm;

public record ChatRequest(String prompt) {
}
```

```java
package cn.edu.softarch.assignment2.llm;

public record ChatResponse(String content, int inputTokens, int outputTokens) {
}
```

```java
package cn.edu.softarch.assignment2.llm;

public class FakeChatGateway implements ChatGateway {
    @Override
    public ChatResponse chat(ChatRequest request) {
        String content = """
                Fake response derived from prompt:
                %s

                Decisions:
                - Keep output constrained to assignment knowledge.

                Self-check: external knowledge avoided.
                """.formatted(request.prompt());
        int inputTokens = Math.max(1, request.prompt().length() / 4);
        int outputTokens = Math.max(1, content.length() / 4);
        return new ChatResponse(content, inputTokens, outputTokens);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
mvn test -Dtest=FakeChatGatewayTest
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/cn/edu/softarch/assignment2/llm src/test/java/cn/edu/softarch/assignment2/llm
git commit -m "feat: introduce testable chat gateway"
```

---

### Task 6: ADD Multi-Agent Workflow

**Files:**
- Create: `src/main/java/cn/edu/softarch/assignment2/workflow/AddWorkflow.java`
- Test: `src/test/java/cn/edu/softarch/assignment2/workflow/AddWorkflowTest.java`

- [ ] **Step 1: Write failing workflow test**

```java
package cn.edu.softarch.assignment2.workflow;

import cn.edu.softarch.assignment2.agent.AgentPromptFactory;
import cn.edu.softarch.assignment2.domain.AgentRole;
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
        assertThat(result.turns()).extracting("agentRole")
                .contains(AgentRole.COORDINATOR, AgentRole.ARCHITECT, AgentRole.QUALITY_ANALYST, AgentRole.REVIEWER, AgentRole.RECORDER);
        assertThat(result.finalReportDraft()).contains("Iteration 1");
        assertThat(result.finalReportDraft()).contains("Iteration 4");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn test -Dtest=AddWorkflowTest
```

Expected: FAIL because `AddWorkflow` does not exist.

- [ ] **Step 3: Implement `AddWorkflow`**

```java
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

    public WorkflowResult run() {
        List<ConversationTurn> turns = new ArrayList<>();
        String context = "ADD Step 1 reviewed all provided inputs: ADD method, Hotel Pricing System case, quality attributes, concerns, constraints, and selected multi-agent setup.";
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
                    String prompt = promptFactory.promptFor(role, iteration, step, context);
                    var response = chatGateway.chat(new ChatRequest(prompt));
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
                    report.append("**").append(role.name()).append("**\n\n")
                            .append(response.content())
                            .append("\n\n");
                }
            }
        }

        return new WorkflowResult(List.copyOf(turns), report.toString());
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
mvn test -Dtest=AddWorkflowTest
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/cn/edu/softarch/assignment2/workflow src/test/java/cn/edu/softarch/assignment2/workflow
git commit -m "feat: orchestrate four-iteration multi-agent ADD workflow"
```

---

### Task 7: Conversation Log Writer

**Files:**
- Create: `src/main/java/cn/edu/softarch/assignment2/logging/ConversationLogWriter.java`
- Test: `src/test/java/cn/edu/softarch/assignment2/logging/ConversationLogWriterTest.java`

- [ ] **Step 1: Write failing log writer test**

```java
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
                new ConversationTurn(Instant.parse("2026-05-27T10:00:00Z"), 1, AddStep.STEP_2_SELECT_DRIVERS, AgentRole.ARCHITECT, "prompt", "response")
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
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn test -Dtest=ConversationLogWriterTest
```

Expected: FAIL because `ConversationLogWriter` does not exist.

- [ ] **Step 3: Implement `ConversationLogWriter`**

```java
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

public class ConversationLogWriter {
    private final Path outputDirectory;
    private final ObjectMapper objectMapper;

    public ConversationLogWriter(Path outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.objectMapper = new ObjectMapper()
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
```

- [ ] **Step 4: If `JavaTimeModule` is missing, add dependency**

Modify `pom.xml` inside `<dependencies>`:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

- [ ] **Step 5: Run test to verify it passes**

Run:

```bash
mvn test -Dtest=ConversationLogWriterTest
```

Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add pom.xml src/main/java/cn/edu/softarch/assignment2/logging src/test/java/cn/edu/softarch/assignment2/logging
git commit -m "feat: write timestamped conversation logs"
```

---

### Task 8: Real Qwen3-Max Adapter and Runtime Configuration

**Files:**
- Create: `src/main/java/cn/edu/softarch/assignment2/config/AssignmentProperties.java`
- Create: `src/main/java/cn/edu/softarch/assignment2/config/QwenConfig.java`
- Create: `src/main/java/cn/edu/softarch/assignment2/llm/QwenChatGateway.java`
- Create: `src/main/resources/application.yml`
- Test: `src/test/java/cn/edu/softarch/assignment2/config/AssignmentPropertiesTest.java`

- [ ] **Step 1: Write configuration properties test**

```java
package cn.edu.softarch.assignment2.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.env.MockEnvironment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentPropertiesTest {
    @Test
    void bindsAssignmentPropertiesFromYaml() throws Exception {
        MockEnvironment environment = new MockEnvironment();
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        List<PropertySource<?>> sources = loader.load("application", new ClassPathResource("application.yml"));
        sources.forEach(environment.getPropertySources()::addLast);

        AssignmentProperties properties = Binder.get(environment)
                .bind("assignment", Bindable.of(AssignmentProperties.class))
                .orElseThrow();

        assertThat(properties.model()).isEqualTo("qwen3-max");
        assertThat(properties.outputDir()).isEqualTo("out/assignment2");
    }
}
```

- [ ] **Step 2: Create `application.yml`**

```yaml
spring:
  application:
    name: assignment2-multi-agent-add
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY:}
      chat:
        options:
          model: qwen3-max
          temperature: 0.2

assignment:
  model: qwen3-max
  output-dir: out/assignment2
```

- [ ] **Step 3: Implement `AssignmentProperties`**

```java
package cn.edu.softarch.assignment2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "assignment")
public record AssignmentProperties(String model, String outputDir) {
}
```

- [ ] **Step 4: Implement `QwenChatGateway` using Spring AI `ChatClient`**

```java
package cn.edu.softarch.assignment2.llm;

import org.springframework.ai.chat.client.ChatClient;

public class QwenChatGateway implements ChatGateway {
    private final ChatClient chatClient;

    public QwenChatGateway(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        String content = chatClient.prompt()
                .user(request.prompt())
                .call()
                .content();
        int inputTokens = Math.max(1, request.prompt().length() / 4);
        int outputTokens = Math.max(1, content.length() / 4);
        return new ChatResponse(content, inputTokens, outputTokens);
    }
}
```

- [ ] **Step 5: Implement `QwenConfig`**

```java
package cn.edu.softarch.assignment2.config;

import cn.edu.softarch.assignment2.llm.ChatGateway;
import cn.edu.softarch.assignment2.llm.QwenChatGateway;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QwenConfig {
    @Bean
    ChatGateway chatGateway(ChatClient.Builder builder) {
        return new QwenChatGateway(builder.build());
    }
}
```

- [ ] **Step 6: Run configuration test**

Run:

```bash
mvn test -Dtest=AssignmentPropertiesTest
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/cn/edu/softarch/assignment2/config src/main/java/cn/edu/softarch/assignment2/llm/QwenChatGateway.java src/main/resources/application.yml src/test/java/cn/edu/softarch/assignment2/config
git commit -m "feat: configure Qwen3-Max DashScope gateway"
```

---

### Task 9: CLI Runner

**Files:**
- Create: `src/main/java/cn/edu/softarch/assignment2/cli/RunAssignmentCommand.java`
- Modify: `src/main/java/cn/edu/softarch/assignment2/config/QwenConfig.java`
- Test: `src/test/java/cn/edu/softarch/assignment2/cli/RunAssignmentCommandTest.java`

- [ ] **Step 1: Write command test with fake gateway bean**

```java
package cn.edu.softarch.assignment2.cli;

import cn.edu.softarch.assignment2.llm.ChatGateway;
import cn.edu.softarch.assignment2.llm.FakeChatGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.shell.test.ShellTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RunAssignmentCommandTest {
    @Autowired
    ShellTestClient client;

    @Test
    void runAssignmentCommandReturnsOutputDirectory() {
        ShellTestClient.NonInteractiveShellSession session = client
                .nonInterative("run-assignment --output-dir target/test-assignment-output")
                .run();

        assertThat(session.screen().lines()).anyMatch(line -> line.contains("target/test-assignment-output"));
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
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn test -Dtest=RunAssignmentCommandTest
```

Expected: FAIL because the command does not exist. If `ShellTestClient` package differs in the installed Spring Shell version, inspect the generated dependency Javadocs and replace only the test import path, not the command behavior.

- [ ] **Step 3: Implement `RunAssignmentCommand`**

```java
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

    @ShellMethod(key = "run-assignment", value = "Run the multi-agent ADD workflow for Assignment 2.")
    public String runAssignment(
            @ShellOption(defaultValue = ShellOption.NULL, help = "Output directory for logs and report draft.") String outputDir
    ) {
        String resolvedOutputDir = outputDir == null ? properties.outputDir() : outputDir;
        var knowledgeBase = KnowledgeBase.fromClasspath();
        var workflow = new AddWorkflow(new AgentPromptFactory(knowledgeBase), chatGateway);
        var result = workflow.run();
        new ConversationLogWriter(Path.of(resolvedOutputDir)).write(result);
        return "Assignment run complete. Outputs written to " + resolvedOutputDir;
    }
}
```

- [ ] **Step 4: If tests fail due to `QwenConfig` creating a real bean during tests, guard it with a profile**

Modify `QwenConfig`:

```java
package cn.edu.softarch.assignment2.config;

import cn.edu.softarch.assignment2.llm.ChatGateway;
import cn.edu.softarch.assignment2.llm.QwenChatGateway;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class QwenConfig {
    @Bean
    ChatGateway chatGateway(ChatClient.Builder builder) {
        return new QwenChatGateway(builder.build());
    }
}
```

- [ ] **Step 5: Run command test**

Run:

```bash
mvn test -Dtest=RunAssignmentCommandTest
```

Expected: PASS and `target/test-assignment-output/conversation-log.md` exists.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/cn/edu/softarch/assignment2/cli src/main/java/cn/edu/softarch/assignment2/config/QwenConfig.java src/test/java/cn/edu/softarch/assignment2/cli
git commit -m "feat: add assignment CLI runner"
```

---

### Task 10: Real Run, Documentation, and Delivery Checklist

**Files:**
- Create: `README.md`
- Create: `docs/DELIVERY-CHECKLIST.md`
- Modify: `src/main/resources/prompts/system-policy.md`

- [ ] **Step 1: Add README**

```markdown
# Assignment 2 Multi-Agent ADD Source Code

This project implements the selected assignment setup:

- Assignment completion method: Multi-agent
- Basic LLM: Qwen3-Max
- Agent framework: Spring AI Alibaba

The program runs four ADD 3.0 iterations for the Hotel Pricing System and writes:

- `conversation-log.jsonl`: complete timestamped machine-readable log
- `conversation-log.md`: complete timestamped human-readable log
- `report-draft.md`: draft content for the English report template

## Requirements

- Java 17
- Maven 3.9+
- Alibaba Cloud DashScope API key with access to Qwen3-Max

## Run Tests

```bash
mvn test
```

## Run With Qwen3-Max

```bash
export DASHSCOPE_API_KEY="your-api-key"
mvn spring-boot:run -Dspring-boot.run.arguments="run-assignment --output-dir out/assignment2"
```

## Assignment Constraints Implemented

- Uses only the provided ADD 3.0 and Hotel Pricing System knowledge files.
- Uses multiple agent roles: coordinator, architect, quality analyst, reviewer, recorder.
- Produces Mermaid or PlantUML views when the ADD step creates or updates views.
- Records complete prompts and responses with timestamps.
- Keeps all decision rules explicit in prompts and outputs.
```

- [ ] **Step 2: Add delivery checklist**

```markdown
# Delivery Checklist

- [ ] `mvn test` passes.
- [ ] `DASHSCOPE_API_KEY` is set before the real run.
- [ ] `mvn spring-boot:run -Dspring-boot.run.arguments="run-assignment --output-dir out/assignment2"` completes.
- [ ] `out/assignment2/conversation-log.jsonl` exists.
- [ ] `out/assignment2/conversation-log.md` exists and includes timestamps.
- [ ] `out/assignment2/report-draft.md` exists.
- [ ] Report is compiled in English and does not exceed 30 A4 pages.
- [ ] Source code is included in the final submission.
- [ ] Conversation logs cover all four iterations.
- [ ] Individual reflection lists each member's Chinese name and contribution.
```

- [ ] **Step 3: Strengthen `system-policy.md` with report language rule**

Append this section:

```markdown
Report rule:
- The final submitted report must be written in English.
- The source code comments and runtime logs may be English.
- The implementation must not rely on knowledge outside the assignment PDF.
```

- [ ] **Step 4: Run all tests**

Run:

```bash
mvn test
```

Expected: all tests PASS.

- [ ] **Step 5: Run a fake end-to-end workflow if test profile exists**

Run:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test -Dspring-boot.run.arguments="run-assignment --output-dir target/fake-run"
```

Expected: command prints `Assignment run complete` and writes `target/fake-run/conversation-log.md`.

- [ ] **Step 6: Run the real Qwen3-Max workflow**

Run:

```bash
export DASHSCOPE_API_KEY="replace-with-real-key"
mvn spring-boot:run -Dspring-boot.run.arguments="run-assignment --output-dir out/assignment2"
```

Expected: command prints `Assignment run complete` and writes logs under `out/assignment2`.

- [ ] **Step 7: Commit**

```bash
git add README.md docs/DELIVERY-CHECKLIST.md src/main/resources/prompts/system-policy.md
git commit -m "docs: document assignment run and delivery checklist"
```

---

## Self-Review

### Spec Coverage

- Multi-agent completion method: covered by `AgentRole`, `AgentPromptFactory`, and `AddWorkflow`.
- Qwen3-Max: covered by `application.yml`, `AssignmentProperties`, and `QwenChatGateway`.
- Spring AI Alibaba: covered by Maven dependency `spring-ai-alibaba-starter-dashscope` and `QwenChatGateway`.
- ADD 3.0: covered by `add-3.0.md`, `IterationPlan`, and workflow steps.
- Hotel Pricing System only: covered by `hotel-pricing-system.md` and prompt policy.
- No external knowledge: covered by `system-policy.md` and prompt tests.
- Four iterations: covered by `IterationPlanTest` and `AddWorkflowTest`.
- Mermaid/PlantUML views: covered by `system-policy.md` and prompt factory output.
- Complete timestamped logs: covered by `ConversationLogWriterTest`.
- Source-code deliverable: covered by Maven project, README, and delivery checklist.

### Placeholder Scan

No implementation step uses unresolved placeholders, empty method bodies, or undefined future behavior. The only replaceable value is the real API key in a shell command, which must remain secret and cannot be committed.

### Type Consistency

The plan consistently uses:

- `AgentRole`
- `AddStep`
- `IterationPlan.assignmentDefault()`
- `ChatGateway.chat(ChatRequest)`
- `ChatResponse.content()`
- `WorkflowResult.turns()`
- `ConversationLogWriter.write(WorkflowResult)`
