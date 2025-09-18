package com.exampleDemo.newsHound.repository;

import com.exampleDemo.newsHound.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {

    Article findByExternalId(String externalId);
}
