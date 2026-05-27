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
