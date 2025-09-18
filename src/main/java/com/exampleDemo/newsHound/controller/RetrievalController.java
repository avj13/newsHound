package com.exampleDemo.newsHound.controller;

import com.exampleDemo.newsHound.model.Article;
import com.exampleDemo.newsHound.dto.NewsArticleDto;
import com.exampleDemo.newsHound.dto.SummaryResponse;
import com.exampleDemo.newsHound.model.QueryRequest;
import com.exampleDemo.newsHound.model.QueryResponse;
import com.exampleDemo.newsHound.service.QueryIntentService;
import com.exampleDemo.newsHound.service.RetrievalService;
import com.exampleDemo.newsHound.service.impl.OpenAISummarizerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.HeaderMap;

import java.util.*;

@RestController
@RequestMapping("/api/v1/news")
public class RetrievalController {

    @Autowired
    RetrievalService retrievalService;

    @Autowired
    OpenAISummarizerService openAISummarizerService;

    @Autowired
    QueryIntentService queryIntentService;

    @RequestMapping("/")
    public ResponseEntity<?> relevantNews(@RequestParam String user_query, HttpServletRequest request) throws Exception {
            Map<String, List<String>> params = new HashMap<>();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                params.put(paramName, List.of(request.getParameterValues(paramName)));
            }
        String intent = "";

        QueryResponse queryResponse = queryIntentService.processQuery(new QueryRequest(user_query));
        List<String> intents = queryResponse.getIntents().stream().map(Object::toString).toList();


        List<Article> articles = new ArrayList<>();

        if(intent.equals("category")){
//            List<String> categories = List.of(user_query);

            List<String> categories = (params.get("category"));
            articles = retrievalService.getNewsByCategory(categories);
        }
        else if (intent.equals("search")) {
            List<String> query = List.of(user_query);
            List<String> location = params.get("location");
            query.addAll(location);
            articles = retrievalService.getNewsBySearch(query);
        }
        else if (intent.equals("source")) {
//            List<String> source = List.of(user_query);
            List<String> source = params.get("source");
            articles = retrievalService.getNewsBySource(source);
        }
        else if (intent.equals("nearby")) {
            double lat = 0.0, lon = 0.0, radius = 50.0;
            lat = Double.parseDouble(params.get("lat").get(0));
            lon = Double.parseDouble(params.get("lon").get(0));
            radius = Double.parseDouble(params.get("radius").get(0));
            articles = retrievalService.getNewsByLocation(lat, lon, radius);
        }
        else if (intent.equals("score")) {
            double relevant_score;
            relevant_score = Double.parseDouble((params.get("relevant_score").get(0)));
            if(relevant_score <= 0.0 || relevant_score > 1.0 || Double.isNaN(relevant_score))
                relevant_score = 0.7;
            articles = retrievalService.getNewsByScore(relevant_score);
        }

        List<NewsArticleDto> newsArticleList = new ArrayList<>();
        for (Article art: articles){
            NewsArticleDto dto = new NewsArticleDto();
            dto.setTitle(art.getTitle());
            dto.setDescription(art.getDescription());
            dto.setUrl(art.getUrl());
            dto.setPublicationDate(art.getPublicationDate().toString());
            dto.setSourceName(art.getSourceName());
            dto.setCategory(art.getCategory());

            String summary = openAISummarizerService.summarize(art);
            dto.setSummary(summary);

            newsArticleList.add(dto);
        }

        return ResponseEntity.ok(newsArticleList);
    }

    // category
//    @GetMapping(name = "/category")
//    private List<Article> getNewsViaCategory(@RequestParam List<String> categories){
//        return retrievalService.getNewsByCategory(categories);
//    }
//
//    @GetMapping(name = "/score")
//    private List<Article> getNewsViaScore(@RequestParam double relevant_score){
//        return retrievalService.getNewsByScore(relevant_score);
//    }
//
//    @GetMapping(name = "/search")
//    private List<Article> getNewsViaSearch(@RequestParam List<String> query, @RequestParam(required = false) List<String> location){
//        query.addAll(location);
//        return retrievalService.getNewsBySearch(query);
//    }
//
//    @GetMapping(name = "/source", produces = "application/json")
//    private List<Article> getNewsViaSource(@RequestParam List<String> source ){
//        return retrievalService.getNewsBySource(source);
//    }
//
//    @GetMapping("/nearby")
//    private List<Article> getNewsViaLocation(@RequestParam double lat, @RequestParam double lon, @RequestParam double radius){
//        return retrievalService.getNewsByLocation(lat, lon, radius);
//    }

    @GetMapping("/summarize")
    public SummaryResponse summarizeArticle(@RequestBody Article request) {
        String summary = openAISummarizerService.summarize(request);
        return new SummaryResponse(summary);
    }

}
