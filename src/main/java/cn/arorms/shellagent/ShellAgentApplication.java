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
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ShellAgentApplication {

    public static void main(String[] args) {
        try {
            // Initialize chat components
            OpenAiChatModel chatModel = createChatModel();
            ChatClient chatClient = ChatClient.create(chatModel);
            Scanner scanner = new Scanner(System.in);

            System.out.println("""
                \n=== Streaming Chat Shell ===
                Type your messages below. Special commands:
                /exit         - Quit the program
                /help         - Show this help
                """);

            // Main conversation loop
            while (true) {
                System.out.print("\nYou: ");
                String userInput = scanner.nextLine().trim();

                // Handle commands
                if (userInput.startsWith("/")) {
                    if (userInput.equalsIgnoreCase("/exit")) {
                        break;
                    }
                    if (userInput.equalsIgnoreCase("/help")) {
                        System.out.println("""
                            Available commands:
                            /exit         - Quit the program
                            /help         - Show this help
                            """);
                        continue;
                    }
                    System.out.println("Unknown command. Type /help for available commands");
                    continue;
                }

                // Empty input handling
                if (userInput.isEmpty()) {
                    System.out.println("Please enter a message or command");
                    continue;
                }

                // Streaming conversation
                System.out.print("\nAI: ");
                CountDownLatch latch = new CountDownLatch(1);

                try {
                    Flux<String> response = chatClient.prompt()
                            .user(userInput)
                            .stream()
                            .content();

                    response.subscribe(
                            System.out::print,
                            error -> {
                                System.err.println("\nError: " + error.getMessage());
                                latch.countDown();
                            },
                            () -> {
                                System.out.println();
                                latch.countDown();
                            }
                    );

                    // Wait for response to complete
                    latch.await();
                } catch (Exception e) {
                    System.err.println("\nError during conversation: " + e.getMessage());
                }
            }

            scanner.close();
            System.out.println("\n=== Chat session ended ===");
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static OpenAiChatModel createChatModel() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl("http://localhost:11434/v1")
                .apiKey("NONE")
                .headers(headers)
                .completionsPath("/chat/completions")
                .embeddingsPath("/embeddings")
                .restClientBuilder(RestClient.builder())
                .webClientBuilder(WebClient.builder())
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gemma3")
                        .temperature(0.7)
                        .streamUsage(true)
                        .build())
                .build();
    }
}