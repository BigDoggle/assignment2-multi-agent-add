package cn.edu.softarch.assignment2.llm;

/**
 * Provides a narrow boundary between deterministic workflow code and chat models.
 */
public interface ChatGateway {
    ChatResponse chat(ChatRequest request);
}
