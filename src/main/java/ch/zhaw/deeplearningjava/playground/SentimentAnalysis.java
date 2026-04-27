package ch.zhaw.deeplearningjava.playground;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

public class SentimentAnalysis {

    private static final Logger logger = LoggerFactory.getLogger(SentimentAnalysis.class);
    private Predictor<String, Classifications> predictor;

    // Der Konstruktor lädt das Modell einmalig in den Arbeitsspeicher
    public SentimentAnalysis() {
        try {
            Criteria<String, Classifications> criteria = Criteria.builder()
                    .optApplication(Application.NLP.SENTIMENT_ANALYSIS)
                    .setTypes(String.class, Classifications.class)
                    .optDevice(Device.cpu())
                    .optProgress(new ProgressBar())
                    .build();
            ZooModel<String, Classifications> model = criteria.loadModel();
            this.predictor = model.newPredictor();
            logger.info("Sentiment-Modell erfolgreich geladen!");
        } catch (Exception e) {
            logger.error("Fehler beim Laden des Modells", e);
        }
    }

    // Diese Methode wird von deinem Controller aufgerufen
    public Classifications predict(String text) throws Exception {
        if (predictor == null) {
            throw new RuntimeException("Fehler: Modell ist nicht geladen.");
        }
        return predictor.predict(text);
    }
}