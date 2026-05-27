package cn.edu.softarch.assignment2.llm;

/**
 * Deterministic test double for workflow and prompt tests.
 */
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
