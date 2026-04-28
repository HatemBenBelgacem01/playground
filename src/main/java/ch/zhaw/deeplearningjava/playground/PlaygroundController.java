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

    @GetMapping("/analyze")
    public Map<String, Double> analyzeText(@RequestParam String text) throws Exception {
        if (predictor == null) {
            throw new RuntimeException("Modell nicht bereit.");
        }
        
        Classifications result = predictor.predict(text);
        Map<String, Double> jsonResponse = new LinkedHashMap<>();
        
        for (var item : result.items()) {
            jsonResponse.put(item.getClassName(), item.getProbability());
        }
        
        return jsonResponse;
    }
}