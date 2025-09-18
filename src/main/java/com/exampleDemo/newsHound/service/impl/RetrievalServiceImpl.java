package com.exampleDemo.newsHound.service.impl;

import com.exampleDemo.newsHound.model.Article;
import com.exampleDemo.newsHound.repository.RetrievalRepository;
import com.exampleDemo.newsHound.service.RetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RetrievalServiceImpl implements RetrievalService {

    private static final double RELEVANT_SCORE_THRESHOLD = 0.7;
    private static final long MAX_LIMIT = 5;


    @Autowired
    private RetrievalRepository retrievalRepository;
    
    @Override
    public List<Article> getNewsByCategory(List<String> categories) {
        List<Article> matchingArticles = new ArrayList<>();
        for(String category : categories){
            retrievalRepository.findAll().stream()
                    .filter(article -> article.getCategory() != null
                            // todo check this
                            && article.getCategory().stream().findAny().orElse("").equalsIgnoreCase(category)
                            && article.getRelevanceScore() >= RELEVANT_SCORE_THRESHOLD
                    )
                    .forEach(matchingArticles::add);
        }

//        matchingArticles.sort((a1, a2) -> a2.getPublicationDate().compareTo(a1.getPublicationDate()));
        List<Article> outputList = matchingArticles.stream().sorted(Comparator.comparing(Article::getPublicationDate).reversed())
                .limit(MAX_LIMIT)
                .toList();

        return outputList;

    }

    @Override
    public List<Article> getNewsByScore(double relevantScore) {
        List<Article> matchingArticles = new ArrayList<>();
        retrievalRepository.findAll().stream()
                .filter(article -> article.getRelevanceScore() >= relevantScore)
                .forEach(matchingArticles::add);

        List<Article> outputList = matchingArticles.stream().sorted((a1,a2)-> Double.compare(a2.getRelevanceScore(), a1.getRelevanceScore()))
                .limit(MAX_LIMIT) // to limit too much data
                .toList();

        return matchingArticles;
    }

    @Override
    public List<Article> getNewsBySearch(List<String> queries) {
//        if (query == null || query.isEmpty())
//            // You can adjust the weight of textScore if needed
//            return articles.stream().a.setRelevanceScore(a.getRelevanceScore() + textScore);
//
//        .sorted((a1, a2) -> Double.compare(a2.getRelevanceScore(), a1.getRelevanceScore()))
//                .limit(MAX_LIMIT)
//                .toList();
//    }

        String searchText = String.join(" ", queries);

        // Fetch from MongoDB with text score
        List<Article> rawResults = retrievalRepository.searchByText(searchText);

        // Rank by relevanceScore + textScore
        return rawResults.stream()
                .sorted(Comparator
                        .comparingDouble((Article a) ->
                                (a.getRelevanceScore() * 0.7) + // weight relevanceScore
                                (a.getTextScore() == null ? 0.0 : a.getTextScore() * 0.3)) // weight textScore
                        .reversed())
                .limit(MAX_LIMIT)
                .toList();
    }



    public List<Article> getNewsByLocation(double lat, double lon, double radius) {
        // Haversine formula to calculate distance between two lat/lon points
        final int EARTH_RADIUS_KM = 6371; // Radius of the earth in km

        List<Article> matchingArticles = new ArrayList<>();
        retrievalRepository.findAll().stream()
                .filter(article -> {
                    if ((article.getLatitude() == 0 ) || ( article.getLongitude() == 0)) {
                        return false;
                    }
                    double dLat = Math.toRadians(article.getLatitude() - lat);
                    double dLon = Math.toRadians(article.getLongitude() - lon);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(article.getLatitude())) *
                                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    double distance = EARTH_RADIUS_KM * c; // Distance in km
                    article.setRadialDistance(distance);
                    return distance <= radius;
                })
                .forEach(matchingArticles::add);

        List<Article> outputList = matchingArticles.stream().sorted(Comparator.comparing(Article::getRadialDistance).reversed())
                .limit(MAX_LIMIT)
                .toList();

        return outputList;
    }

    @Override
    public List<Article> getNewsBySource(List<String> source) {
        List<Article> matchingArticles = new ArrayList<>();
        for(String src : source){
            retrievalRepository.findAll().stream()
                    .filter(article -> article.getSourceName() != null
                            && article.getSourceName().equalsIgnoreCase(src)
                            && article.getRelevanceScore() >= RELEVANT_SCORE_THRESHOLD
                    )
                    .forEach(matchingArticles::add);
        }

        List<Article> outputList = matchingArticles.stream().sorted(Comparator.comparing(Article::getPublicationDate).reversed())
                .limit(MAX_LIMIT)
                .toList();

        return outputList;

    }
}












