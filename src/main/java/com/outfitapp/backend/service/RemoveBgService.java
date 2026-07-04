package com.outfitapp.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RemoveBgService {

    @Value("${removebg.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.remove.bg/v1.0")
            .build();

    public byte[] eliminarFondo(byte[] imagenBytes, String nombreArchivo) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image_file", new ByteArrayResource(imagenBytes) {
            @Override
            public String getFilename() { return nombreArchivo; }
        }, MediaType.APPLICATION_OCTET_STREAM);
        builder.part("size", "auto");

        return webClient.post()
                .uri("/removebg")
                .header("X-Api-Key", apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}