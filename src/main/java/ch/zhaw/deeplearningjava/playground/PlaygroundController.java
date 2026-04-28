package ch.zhaw.deeplearningjava.playground;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PlaygroundController {

    @GetMapping("/analyze")
    public Map<String, Double> analyzeText(@RequestParam String text) throws Exception {
        // Die URL zeigt auf das Sentiment-Modell im DJL Serving Container
        var uri = "http://localhost:8081/predictions/sentiment_analysis"; 
        
        if (this.isDockerized()) {
            uri = "http://model-service:8080/predictions/sentiment_analysis";
        }

        var webClient = WebClient.create();
        
        // Sende den Text als simplen String an DJL Serving
        String jsonResponse = webClient.post()
            .uri(uri)
            .header("Content-Type", "text/plain")
            .body(BodyInserters.fromValue(text))
            .retrieve()
            .bodyToMono(String.class)
            .block();
            
        // DJL Serving gibt ein Array zurück: [{"class": "Positive", "probability": 0.99}]
        // Wir wandeln es um, damit dein HTML/JS Frontend (script.js) unverändert weiterfunktioniert!
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> djlResult = mapper.readValue(jsonResponse, new TypeReference<List<Map<String, Object>>>(){});
        
        Map<String, Double> finalResult = new LinkedHashMap<>();
        for (Map<String, Object> item : djlResult) {
            String className = item.containsKey("class") ? (String) item.get("class") : (String) item.get("className");
            Double probability = ((Number) item.get("probability")).doubleValue();
            finalResult.put(className, probability);
        }
        
        return finalResult;
    }

    private boolean isDockerized() {
        File f = new File("/.dockerenv");
        return f.exists();
    }
}