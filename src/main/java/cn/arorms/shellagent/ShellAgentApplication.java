package cn.arorms.shellagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShellAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShellAgentApplication.class, args);
    }

    public void run(String... args) throws Exception {
        System.out.println("ShellAgentApplication.run");
    }
}
