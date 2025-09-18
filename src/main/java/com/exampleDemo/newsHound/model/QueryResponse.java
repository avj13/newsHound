package com.exampleDemo.newsHound.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QueryResponse {
    private String query;
    private List<Object> entities;
    private List<Object> intents;
}

