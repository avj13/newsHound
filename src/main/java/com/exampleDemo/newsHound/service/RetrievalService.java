package com.exampleDemo.newsHound.service;

import com.exampleDemo.newsHound.model.Article;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RetrievalService {
    List<Article> getNewsByCategory(List<String> categories);

    List<Article> getNewsByScore(double relevantScore);

    List<Article> getNewsBySearch(List<String> query);

    List<Article> getNewsByLocation(double lat, double lon, double radius);

    List<Article> getNewsBySource(List<String> source);
}
