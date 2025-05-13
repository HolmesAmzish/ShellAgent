package cn.arorms.shellagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;

public class ConsoleApplication {
    public static void main(String[] args) {
        String ollamaBaseUrl = "http://localhost:11434/v1"; // Ollama 的 OpenAI 兼容 API 地址
        String ollamaApiKey = "NONE";

        HttpHeaders headers = new HttpHeaders(); // 使用 HttpHeaders
        headers.setContentType(MediaType.APPLICATION_JSON); // 现在可以使用 setContentType


        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return !response.getStatusCode().is2xxSuccessful();
            }
        };

        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(ollamaBaseUrl)
                .apiKey(ollamaApiKey)
                .headers(headers)
                .completionsPath("/chat/completions") // 确保路径与 Ollama 兼容
                .embeddingsPath("/embeddings")       // 确保路径与 Ollama 兼容
                .restClientBuilder(RestClient.builder())
                .webClientBuilder(WebClient.builder())
                .responseErrorHandler(responseErrorHandler)
                .build();

        ChatClient chatClient = new ChatClient(openAiApi);
        Prompt prompt = new Prompt(new UserMessage("请介绍一下东京。"));

        // 调用 AI 接口
        String response = chatClient.prompt(prompt).toString();

        System.out.println("OpenAiApi object created successfully for Ollama.");

    }
}