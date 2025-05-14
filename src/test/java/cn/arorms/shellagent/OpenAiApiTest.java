package cn.arorms.shellagent;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OpenAiApiTest {
    public static void main(String[] args) {

        String ollamaBaseUrl = "http://localhost:11434/v1";
        String ollamaApiKey = "NONE"; // Ollama通常不需要API key

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(ollamaBaseUrl)
                .apiKey(ollamaApiKey)
                .headers(headers)
                .completionsPath("/chat/completions")
                .embeddingsPath("/embeddings")
                .restClientBuilder(RestClient.builder())
                .webClientBuilder(WebClient.builder())
                .build();

        List<ChatCompletionMessage> messages = new ArrayList<>();
        messages.add(new ChatCompletionMessage("Why the sky is blue", ChatCompletionMessage.Role.USER));

        try {
            OpenAiApi.ChatCompletionRequest chatRequest = new OpenAiApi.ChatCompletionRequest(
                    messages,
                    "gemma3:1b",
                    0.7,
                    false
            );

            ResponseEntity<OpenAiApi.ChatCompletion> response =
                    openAiApi.chatCompletionEntity(chatRequest);

            if (response.getStatusCode().is2xxSuccessful()) {
                String content = Objects.requireNonNull(response.getBody()).choices().getFirst().message().content();
                System.out.println("AI response: " + content);
            } else {
                System.err.println("Request failed: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}