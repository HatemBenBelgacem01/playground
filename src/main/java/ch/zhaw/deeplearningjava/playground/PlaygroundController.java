package ch.zhaw.deeplearningjava.playground;


import java.util.LinkedHashMap;
import java.util.Map;
import ai.djl.Application;
import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaygroundController {
    
    private static final Logger logger = LoggerFactory.getLogger(PlaygroundController.class);
    private Predictor<String, Classifications> predictor;

    // Das Modell wird beim Starten der Applikation einmalig geladen
    public PlaygroundController() {
        try {
            Criteria<String, Classifications> criteria = Criteria.builder()
                    .optApplication(Application.NLP.SENTIMENT_ANALYSIS)
                    .setTypes(String.class, Classifications.class)
                    .optDevice(Device.cpu())
                    .optProgress(new ProgressBar())
                    .build();
            ZooModel<String, Classifications> model = criteria.loadModel();
            this.predictor = model.newPredictor();
            logger.info("Modell erfolgreich geladen!");
        } catch (Exception e) {
            logger.error("Cannot build Predictor", e);
        }
    }

    @GetMapping("/ping")
    public String getPing() {
        return "Hier steht ein Text für die App Playground"; //
    }

    @GetMapping("/number")
    public int getNumber() {
        return 100; //
    }

    // Dein neuer Deep-Learning-Endpoint
    @GetMapping("/analyze")
    public String analyzeText(@RequestParam String text) {
        // Die URL zeigt auf den Namen des Services im Docker-Netzwerk
        var uri = "http://localhost:8081/predictions/sentiment"; 
        
        // Wenn wir im Docker-Netzwerk sind, nutzen wir den Service-Namen
        if (new java.io.File("/.dockerenv").exists()) {
            uri = "http://model-service:8080/predictions/sentiment";
        }

        var webClient = org.springframework.web.reactive.function.client.WebClient.create();
        
        return webClient.post()
            .uri(uri)
            .bodyValue(text)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    private boolean isDockerized() {
        File f = new File("/.dockerenv");
        return f.exists();
    }
}