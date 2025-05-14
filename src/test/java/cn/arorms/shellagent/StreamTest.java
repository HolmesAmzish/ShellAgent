package cn.arorms.shellagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;

public class StreamTest {
    public static void main(String[] args) throws InterruptedException {
        String ollamaBaseUrl = "http://localhost:11434/v1";
        String ollamaApiKey = "NONE";

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

        OpenAiChatModel gemmaModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gemma3")
                        .temperature(0.7)
                        .streamUsage(true)
                        .build())
                .build();

        ChatClient gemmaChatClient = ChatClient.create(gemmaModel);

        Flux<String> output = gemmaChatClient.prompt()
                .user("Why the sky is blue?")
                .stream()
                .content();

        CountDownLatch latch = new CountDownLatch(1);

        output.subscribe(
                System.out::print,
                error -> {
                    System.err.println("Error: " + error);
                    latch.countDown();
                },
                () -> {
                    System.out.println("\n--- Stream Completed ---");
                    latch.countDown();
                }
        );

        latch.await(); // Block until latch.countDown()
    }
}