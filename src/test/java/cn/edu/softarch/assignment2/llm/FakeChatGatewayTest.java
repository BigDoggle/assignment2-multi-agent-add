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
