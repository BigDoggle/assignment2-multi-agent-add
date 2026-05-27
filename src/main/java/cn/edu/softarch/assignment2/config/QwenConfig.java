package cn.edu.softarch.assignment2.config;

import cn.edu.softarch.assignment2.llm.ChatGateway;
import cn.edu.softarch.assignment2.llm.QwenChatGateway;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates the real chat gateway backed by Spring AI Alibaba DashScope.
 */
@Configuration
public class QwenConfig {
    @Bean
    @ConditionalOnMissingBean(ChatGateway.class)
    ChatGateway chatGateway(ChatClient.Builder builder) {
        return new QwenChatGateway(builder.build());
    }
}
