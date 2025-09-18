package com.exampleDemo.newsHound.controller;

import com.exampleDemo.newsHound.model.QueryRequest;
import com.exampleDemo.newsHound.model.QueryResponse;
import com.exampleDemo.newsHound.service.QueryIntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

        import java.io.IOException;

@RestController
@RequestMapping("/api")
public class QueryIntentController {

    @Autowired
    private QueryIntentService service;


    @PostMapping("/extract")
    public QueryResponse extract(@RequestBody QueryRequest request) {
        try {
            return service.processQuery(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
