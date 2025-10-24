package com.example.demo.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class WordFrequencyService {

    private final HashedDictionary<String, Integer> dictionary = new HashedDictionary<>(1663);

    public Map<String, Integer> analyzeFile() {
        // Reset dictionary each time so counts don’t stack between requests
        dictionary.clear();

        try {
            // Load file from resources folder (works even inside packaged JAR)
            File file = new ClassPathResource("usconstitution.txt").getFile();
            try (Scanner reader = new Scanner(file)) {
                while (reader.hasNext()) {
                    String word = reader.next().toLowerCase().replaceAll("[^a-z]", "");
                    if (word.isEmpty()) continue;

                    Integer count = dictionary.getValue(word);
                    dictionary.add(word, (count == null) ? 1 : count + 1);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found", e);
        } catch (Exception e) {
            throw new RuntimeException("Error reading file", e);
        }

        // Convert custom dictionary to LinkedHashMap for easy JSON return
        Map<String, Integer> result = new LinkedHashMap<>();
        Iterator<String> it = dictionary.getKeyIterator();
        while (it.hasNext()) {
            String key = it.next();
            result.put(key, dictionary.getValue(key));
        }

        // Sort by frequency (descending order)
        return result.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }
}
