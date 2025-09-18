package com.exampleDemo.newsHound.service.impl;

import com.exampleDemo.newsHound.model.Article;
import com.exampleDemo.newsHound.service.SummarizeService;

//import com.example.newssummarizer.dto.ArticleRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class OpenAISummarizerService  implements SummarizeService{

    private final OpenAiService openAiService;

//    String apiKey = System.getenv("OPENAI_API_KEY");

    public OpenAISummarizerService(@Value("${openai.api-key}") String apiKey){
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60));
    }

    public String summarize(Article article) {
        String prompt = String.format(
                "Summarize the following news article into a clear, simple, and concise 100-150 word paragraph. " +
                        "Do not start the summary with the date. It only cover the key aspects but also is easy to understand." +
                        "Do not omit vital information such as names, places, and important events. " +
                        "You can access the link of the article given to know more about the article for summarization.\n\n" +
                        "Title: %s\n" +
                        "Description: %s\n" +
                        "URL: %s\n" +
                        "Publication Date: %s\n" +
                        "Source: %s\n" +
                        "Category: %s\n",
                article.getTitle(),
                article.getDescription(),
                article.getUrl(),
                article.getPublicationDate(),
                article.getSourceName(),
                article.getCategory()
        );

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(300)
                .temperature(0.7)
                .build();

        return openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent()
                .trim();
    }
}

