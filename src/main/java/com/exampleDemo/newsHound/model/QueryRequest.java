package com.exampleDemo.newsHound.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QueryRequest {
    private String query;
    private String mode = "llm";        // "llm" or "ner"
    private String precision = "medium"; // "low", "medium", "high"
    private boolean verbose = false;     // default minimal output

    public QueryRequest(String query) {
        this.query = query;
        this.mode = "llm";
        this.precision = "medium";
        this.verbose = false;
    }
}
