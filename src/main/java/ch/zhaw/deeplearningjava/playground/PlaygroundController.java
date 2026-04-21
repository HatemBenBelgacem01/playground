package ch.zhaw.deeplearningjava.playground;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class PlaygroundController {
    
    @GetMapping("/ping")
    public String getPing() {
        return "Hier steht ein Text für die App Playground";
    }

    @GetMapping("/number")
    public int getNumber() {
        return 100;
    }

}
