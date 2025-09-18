package com.exampleDemo.newsHound.service.impl;

import com.exampleDemo.newsHound.model.QueryRequest;
import com.exampleDemo.newsHound.model.QueryResponse;
import com.exampleDemo.newsHound.service.QueryIntentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class QueryIntentServiceImpl implements QueryIntentService {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public QueryResponse processQuery(QueryRequest req) throws IOException {
        return runLLM(req);
    }

    private QueryResponse runLLM(QueryRequest req) throws IOException {
        String prompt = buildPrompt(req.getQuery(), req.getPrecision());

        // Build JSON request
        String requestBody = mapper.writeValueAsString(Map.of(
                "model", "gpt-4o-mini",
                "temperature", 0,
                "messages", List.of(Map.of("role", "system", "content", prompt))
        ));

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            JsonNode root = mapper.readTree(body);
            String raw = root.path("choices").get(0).path("message").path("content").asText();

            JsonNode parsed;
            try {
                parsed = mapper.readTree(raw);
            } catch (Exception e) {
                parsed = mapper.createObjectNode()
                        .putArray("entities").addObject()
                        .putArray("intents")
                        .add("search");
            }

            List<Object> entities;
            List<Object> intents;

            if (req.isVerbose()) {
                entities = mapper.convertValue(parsed.path("entities"), List.class);
                intents = mapper.convertValue(parsed.path("intents"), List.class);
            } else {
                intents = new ArrayList<>();
                entities = new ArrayList<>();
                parsed.path("entities").forEach(e -> entities.add(e.path("text").asText()));
                parsed.path("intents").forEach(i -> intents.add(i.asText()));
            }

            return new QueryResponse(req.getQuery(), entities, intents);
        }
    }


    private String buildPrompt(String query, String precision) {
        String precisionNote;
        switch (precision.toLowerCase()) {
            case "low":
                precisionNote = "Extract only the most critical entities (1-2).";
                break;
            case "high":
                precisionNote = "Extract all possible entities with fine-grained detail.";
                break;
            default:
                precisionNote = "Extract main entities with balanced granularity.";
        }

        return """
        You are an assistant for a news query understanding system.
        
        Task: Given a user query, extract:
        1. Entities (types: person, organization, publisher, category, event, location, other)
        2. Intents: choose from ["category", "score", "search", "source", "nearby"] (up to 3 most relevant)
        
        Output valid JSON only:
        {
          "entities": [{"text": "...", "type": "..."}],
          "intents": ["...", "..."]
        }
        
        %s
        Query: "%s"
        """.formatted(precisionNote, query);
    }
}
