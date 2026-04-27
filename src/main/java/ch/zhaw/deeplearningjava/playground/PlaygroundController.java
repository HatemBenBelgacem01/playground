package ch.zhaw.deeplearningjava.playground;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.File;

@RestController
public class PlaygroundController {

    /**
     * Dieser Endpunkt nimmt den Text entgegen und leitet ihn an den 
     * DJL-Serving Container weiter.
     */
    @GetMapping("/analyze")
    public String analyzeText(@RequestParam String text) {
        // Standard-URL für die lokale Entwicklung (Zugriff auf den gemappten Port)
        var uri = "http://localhost:8081/predictions/sentiment_analysis";
        
        // Falls die App im Docker-Netzwerk läuft, nutzen wir den Namen des Services [cite: 2882, 2886]
        if (this.isDockerized()) {
            uri = "http://model-service:8080/predictions/sentiment_analysis";
        }

        // Erstellung des WebClients für den REST-Aufruf [cite: 2729, 2889]
        var webClient = WebClient.create();
        
        // Weiterleiten der Anfrage als POST-Request an den Modell-Server [cite: 2891, 2892]
        return webClient.post()
            .uri(uri)
            .bodyValue(text)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    /**
     * Hilfsmethode zur Erkennung der Docker-Umgebung [cite: 2874, 2900]
     */
    private boolean isDockerized() {
        File f = new File("/.dockerenv");
        return f.exists();
    }

    @GetMapping("/ping")
    public String getPing() {
        return "DJL Consumer App ist bereit und läuft!";
    }
}