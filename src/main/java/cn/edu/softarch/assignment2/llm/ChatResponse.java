package cn.edu.softarch.assignment2.llm;

/**
 * Immutable response returned by a chat model gateway.
 */
public record ChatResponse(String content, int inputTokens, int outputTokens) {
}
