package com.example.demo.controller;

import com.example.demo.service.WordFrequencyService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class WordController {

    private final WordFrequencyService service;

    // Constructor-based dependency injection
    public WordController(WordFrequencyService service) {
        this.service = service;
    }

    // This endpoint returns the word frequency JSON
    @GetMapping("/analyze")
    public Map<String, Integer> analyze() {
        return service.analyzeFile();
    }
}
