package com.example.backend.DocumentRag.Configuration;


import org.checkerframework.checker.units.qual.C;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class config {

    @Bean("googleAi")
    public ChatClient getGoogleChatClient(GoogleGenAiChatModel chatClient) {
        return ChatClient.builder(chatClient).build();
    }

    @Bean("openAi")
    public ChatClient getOpenAiChatClient(OpenAiChatModel chatClient) {
        return ChatClient.builder(chatClient).build();
    }
}
