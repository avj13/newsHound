package com.exampleDemo.newsHound.service;

import com.exampleDemo.newsHound.model.QueryRequest;
import com.exampleDemo.newsHound.model.QueryResponse;

public interface QueryIntentService {
    public QueryResponse processQuery(QueryRequest req) throws Exception;
}
