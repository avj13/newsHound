package com.exampleDemo.newsHound.repository;

import com.exampleDemo.newsHound.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetrievalRepository  extends MongoRepository<Article, String> {
//    List<Article> findAllByCategory(String category);

    // MongoDB full-text search with text score
//    @Query(value = "{ $text: { $search: ?0 } }", sort = "{ score: { $meta: \"textScore\" } }")

    @Query(value = "{ $text: { $search: ?0 } }",
            fields = "{ score: { $meta: 'textScore' } }",
            sort = "{ score: { $meta: 'textScore' } }")
    List<Article> searchByText(String searchText);
}
