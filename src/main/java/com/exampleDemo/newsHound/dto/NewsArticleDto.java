package com.exampleDemo.newsHound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NewsArticleDto {

    private String title;
    private String description;
    private String url;
    @JsonProperty("publication_date")
    private String publicationDate;
    @JsonProperty("source_name")
    private String sourceName;
    private String summary;
    private List<String> category;
    @JsonProperty("relevance_score")
    private double relevanceScore;
}
