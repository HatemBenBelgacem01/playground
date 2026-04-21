package ch.zhaw.deeplearningjava.playground;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentimentController {
    
    // Hier wird unsere neue Klasse initialisiert
    private SentimentAnalysis analysis = new SentimentAnalysis();

    @GetMapping("/sentiment")
    public String predict(@RequestParam(name="text", required = true) String text) throws Exception {
        // Text an die Logik-Klasse weitergeben
        var result = analysis.predict(text);
        
        // Das Ergebnis mit der DJL-eigenen Methode sicher in JSON umwandeln
        return result.toJson();
    }
}