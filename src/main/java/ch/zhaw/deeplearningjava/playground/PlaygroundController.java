package ch.zhaw.deeplearningjava.playground;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PlaygroundController {

    @GetMapping("/analyze")
    public Map<String, Double> analyzeText(@RequestParam String text) {
        
        // 1. Schlaue Erkennung des DJL-Containers
        String uri = "http://model-service:8080/predictions/sentiment_analysis";
        try {
            // Test, ob der Container im Docker-Netzwerk erreichbar ist
            java.net.InetAddress.getByName("model-service");
        } catch (Exception e) {
            // Fallback für lokale Tests ohne Docker Compose
            uri = "http://localhost:8081/predictions/sentiment_analysis";
        }

        try {
            // 2. Anfrage an die KI senden
            var webClient = WebClient.create();
            String jsonResponse = webClient.post()
                .uri(uri)
                .header("Content-Type", "text/plain")
                .body(BodyInserters.fromValue(text))
                .retrieve()
                .bodyToMono(String.class)
                .block();
                
            // 3. Antwort verarbeiten
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<Map<String, Object>> djlResult = mapper.readValue(jsonResponse, new TypeReference<List<Map<String, Object>>>(){});
                Map<String, Double> finalResult = new LinkedHashMap<>();
                for (Map<String, Object> item : djlResult) {
                    String className = item.containsKey("class") ? String.valueOf(item.get("class")) : String.valueOf(item.get("className"));
                    Double probability = ((Number) item.get("probability")).doubleValue();
                    finalResult.put(className, probability);
                }
                return finalResult;
            } catch (Exception parseEx) {
                // Zeigt das rohe JSON als Balken im Frontend an, falls das Format abweicht!
                Map<String, Double> errorResult = new LinkedHashMap<>();
                errorResult.put("KI-Antwort: " + jsonResponse, 1.0);
                return errorResult;
            }

        } catch (Exception e) {
            // Zeigt Verbindungsfehler direkt als Balken im Frontend an anstatt abzustürzen!
            Map<String, Double> errorResult = new LinkedHashMap<>();
            errorResult.put("Backend-Fehler: " + e.getMessage(), 1.0);
            return errorResult;
        }
    }
}