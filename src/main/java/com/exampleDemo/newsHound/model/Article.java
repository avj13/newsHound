package com.exampleDemo.newsHound.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "articles")
@CompoundIndex(name = "text_index", def = "{'title': 'text', 'description': 'text'}")
public class Article {

    @Id
    @Generated()
    private String id;
    private String externalId;
    private String title;
    @TextIndexed
    private String description;
    @TextIndexed
    private String url;
    private LocalDateTime publicationDate;
    @Indexed
    private String sourceName;
    @Indexed
    private List<String> category;
    private double relevanceScore;
    private double latitude;
    private double longitude;

    @TextScore
    private Float textScore;

    @Transient
    private double radialDistance;
}

