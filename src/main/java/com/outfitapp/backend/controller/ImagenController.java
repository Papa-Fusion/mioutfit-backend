package com.outfitapp.backend.controller;

import com.outfitapp.backend.service.CloudinaryService;
import com.outfitapp.backend.service.RemoveBgService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/imagen")
public class ImagenController {

    private final RemoveBgService removeBgService;
    private final CloudinaryService cloudinaryService;

    public ImagenController(RemoveBgService removeBgService,
                            CloudinaryService cloudinaryService) {
        this.removeBgService = removeBgService;
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarImagen(@RequestParam("file") MultipartFile file) {
        try {
            byte[] imagenOriginal = file.getBytes();
            byte[] imagenSinFondo = removeBgService.eliminarFondo(
                imagenOriginal, file.getOriginalFilename()
            );
            String url = cloudinaryService.subirImagen(
                imagenSinFondo, file.getOriginalFilename()
            );
            return ResponseEntity.ok(Map.of("secure_url", url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "No se pudo procesar la imagen"));
        }
    }
}