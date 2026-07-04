package com.outfitapp.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/clima")
@CrossOrigin(origins = "http://localhost:5173") // Ajusta al puerto de tu frontend si es necesario
public class ClimaController {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @GetMapping
    public ResponseEntity<?> obtenerClima(@RequestParam double lat, @RequestParam double lon) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s", lat, lon, apiKey);
        
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Hacemos la petición a la API externa de forma segura desde el backend
            String resultado = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"No se pudo obtener el clima\"}");
        }
    }
}