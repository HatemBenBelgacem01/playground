package ch.zhaw.deeplearningjava.playground;

public class SentimentController {
    
    private SentimentAnalysis analysis = new SentimentAnalysis();
    
    @GetMapping("/sentiment")
    public String predict(@RequestParam(name="text", required = true) String text) throws Exception {
        var result = analysis.predict(text);
        return result.toJson();
    }
}
