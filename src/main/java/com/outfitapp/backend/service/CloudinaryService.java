package com.outfitapp.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class CloudinaryService {

    @Value("${cloudinary.cloud.name}")
    private String cloudName;

    @Value("${cloudinary.upload.preset}")
    private String uploadPreset;

    public String subirImagen(byte[] imagenBytes, String nombreArchivo) {
        WebClient webClient = WebClient.create(
            "https://api.cloudinary.com/v1_1/" + cloudName
        );

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(imagenBytes) {
            @Override
            public String getFilename() { return nombreArchivo; }
        }, MediaType.APPLICATION_OCTET_STREAM);
        builder.part("upload_preset", uploadPreset);

        Map response = webClient.post()
                .uri("/image/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("secure_url");
    }
}