package com.exampleDemo.newsHound.service.impl;

import com.exampleDemo.newsHound.model.Article;
import com.exampleDemo.newsHound.repository.ArticleRepository;
import com.exampleDemo.newsHound.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String saveArticle(Article art) {
        if(articleRepository.findByExternalId(art.getExternalId()) != null) {
            throw new IllegalArgumentException("Article with externalId " + art.getExternalId() + " already exists");
        }
        return articleRepository.save(art).getId();

    }

    @Override
    public String saveAllArticles(List<Article> articles) {
        String errorMsg = "";
        boolean hasError = false;
        int pos_count =0, neg_count=0;
        for (Article art : articles) {
            if(articleRepository.findByExternalId(art.getExternalId()) == null) {
                articleRepository.save(art);
                pos_count++;
            }
            else {
                errorMsg += "Article with externalId " + art.getExternalId() + " already exists. \n";
                hasError = true;
                neg_count++;
            }
        }
        if (hasError) {
            throw new IllegalArgumentException("Total : " + articles.size() + " , Inserted : " + pos_count + " , Skipped : " + neg_count + "\n" + errorMsg);
        }
        else {
            return "All articles inserted successfully. Total : " + articles.size();
        }
    }

    public String bulkInsert(List<Article> articles) {
        mongoTemplate.insert(articles, Article.class);// 1 call
        return "Total articles inserted: " + articles.size();
    }


    public ResponseEntity<?> getArticleByExternalId(String externalId) {
        Article article = articleRepository.findByExternalId(externalId);
        if (article != null) {
            return ResponseEntity.ok(article);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public void bulkInsertArticles(List<Article> articles) {
        mongoTemplate.insert(articles, Article.class); // 1 call
    }

}
