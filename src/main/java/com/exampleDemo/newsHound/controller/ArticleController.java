package com.exampleDemo.newsHound.controller;

import com.exampleDemo.newsHound.model.Article;
import com.exampleDemo.newsHound.dto.ArticleDto;
import com.exampleDemo.newsHound.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @PostMapping("/save")
    public ResponseEntity<?> saveArticle(@RequestBody ArticleDto articleDto) {

        Article art = getArticleFromDto(articleDto);
        if (!isValidArticle(art)) {
            throw new IllegalArgumentException("Invalid data for 1 or more articles");
        }
        String id = articleService.saveArticle(art);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/saveList")
    public ResponseEntity<?> saveList(@RequestBody List<ArticleDto> articleDtoList) {
        if(articleDtoList.size() > 10){
            throw new IllegalArgumentException("List size exceeds the limit of 10 articles, use Bulk Insert");
        }
        List<Article> articles = articleDtoList.stream().map(articleDto -> {
            Article art = getArticleFromDto(articleDto);
            if (!isValidArticle(art)) {
                throw new IllegalArgumentException("Invalid article data");
            }
            return art;
        }).toList();
        String ids = articleService.saveAllArticles(articles);
        return ResponseEntity.ok(ids);
    }

    @PostMapping("/bulkInsert")
    public ResponseEntity<?> bulkInsert(@RequestBody List<ArticleDto> articleDtoList){
        List<Article> articles = articleDtoList.stream().map(articleDto -> {
            Article art = getArticleFromDto(articleDto);
            if (!isValidArticle(art)) {
                throw new IllegalArgumentException("Invalid article data");
            }
            return art;
        }).toList();
        String output = articleService.bulkInsert(articles);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getArticleByExternalId(@RequestParam String id) {
        return articleService.getArticleByExternalId(id);
    }

    LocalDateTime convertDateTime(String input) {
        return LocalDateTime.parse(input);          //        "2025-03-26T04:46:55"
    }

    private Article getArticleFromDto(ArticleDto articleDto) {
        return Article.builder()
                .externalId(articleDto.getId())
                .title(articleDto.getTitle())
                .description(articleDto.getDescription())
                .url(articleDto.getUrl())
                .publicationDate(convertDateTime(articleDto.getPublicationDate()))
                .sourceName(articleDto.getSourceName())
                .category(articleDto.getCategory())
                .relevanceScore(articleDto.getRelevanceScore())
                .latitude(articleDto.getLatitude())
                .longitude(articleDto.getLongitude())
                .build();
    }

    private boolean isValidArticle(Article art) {
        if (art.getTitle() == null || art.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (art.getUrl() == null || art.getUrl().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (art.getPublicationDate() == null) {
            throw new IllegalArgumentException("Publication date cannot be null");
        }
        if (art.getSourceName() == null || art.getSourceName().isEmpty()) {
            throw new IllegalArgumentException("Source name cannot be null or empty");
        }
        if (art.getCategory() == null || art.getCategory().isEmpty()) {
            throw new IllegalArgumentException("At least one category must be provided");
        }
        if (art.getRelevanceScore() == 0) {
            throw new IllegalArgumentException("Relevance score cannot be null or empty or zero");
        }
        return true;
    }


}
