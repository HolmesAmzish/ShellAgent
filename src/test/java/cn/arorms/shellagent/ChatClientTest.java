package cn.arorms.shellagent;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

public class ChatClientTest {
    public static void main(String[] args) {

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
                        .model("gemma3:1b")
                        .temperature(0.7)
                        .build())
                .build();

        ChatClient gemmaChatClient = ChatClient.create(gemmaModel);

        String response = gemmaChatClient.prompt()
                .user("Hello, can you introduce yourself")
                .call()
                .content();

        System.out.println("Response: " + response);

    }
}