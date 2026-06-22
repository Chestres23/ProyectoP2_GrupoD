package ec.edu.espe.backend.controller;

import ec.edu.espe.backend.service.impl.ReactiveIntervalDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class ReactiveDemoController {

    @Autowired
    private ReactiveIntervalDemo intervalDemo;

    @PostMapping("/interval")
    public ResponseEntity<String> ejecutarInterval() {
        intervalDemo.ejecutarDemoInterval();
        return ResponseEntity.ok("Demo de Flux.interval() iniciada. Revisar logs del servidor.");
    }
}