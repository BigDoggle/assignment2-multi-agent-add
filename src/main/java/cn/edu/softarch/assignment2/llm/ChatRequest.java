package cn.edu.softarch.assignment2.llm;

/**
 * Immutable request passed to a chat model gateway.
 */
public record ChatRequest(String prompt) {
}
