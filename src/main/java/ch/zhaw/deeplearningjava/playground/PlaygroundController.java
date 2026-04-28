package ch.zhaw.deeplearningjava.playground;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class PlaygroundController {

    @GetMapping("/analyze")
    public Map<String, Double> analyzeText(@RequestParam String text) {
        
        String uri = "http://model-service:8080/predictions/sentiment_analysis";
        try {
            java.net.InetAddress.getByName("model-service");
        } catch (Exception e) {
            uri = "http://localhost:8081/predictions/sentiment_analysis";
        }

        try {
            var webClient = WebClient.create();
            String jsonResponse = webClient.post()
                .uri(uri)
                .header("Content-Type", "text/plain")
                .body(BodyInserters.fromValue(text))
                .retrieve()
                .bodyToMono(String.class)
                .block();
                
            // Die KI liefert direkt {"Positive": 0.99}. Das geben wir ans Frontend weiter!
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse, new TypeReference<Map<String, Double>>(){});

        } catch (Exception e) {
            Map<String, Double> errorResult = new LinkedHashMap<>();
            errorResult.put("System-Fehler: " + e.getMessage(), 1.0);
            return errorResult;
        }
    }
}