package cn.edu.softarch.assignment2.llm;

import org.springframework.ai.chat.client.ChatClient;

/**
 * ChatGateway implementation that sends prompts to the configured Qwen model.
 */
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
        int inputTokens = estimateTokens(request.prompt());
        int outputTokens = estimateTokens(content);
        return new ChatResponse(content, inputTokens, outputTokens);
    }

    private int estimateTokens(String text) {
        return Math.max(1, text.length() / 4);
    }
}
