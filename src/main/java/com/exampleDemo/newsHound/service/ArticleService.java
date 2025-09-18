package com.exampleDemo.newsHound.service;

import com.exampleDemo.newsHound.model.Article;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ArticleService {
    String saveArticle(Article art);
    String saveAllArticles(List<Article> articles);

    ResponseEntity<?> getArticleByExternalId(String externalId);

    String bulkInsert(List<Article> articles);
}
