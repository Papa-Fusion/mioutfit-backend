package com.outfitapp.backend.controller;

import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.service.RecomendacionService;
import com.outfitapp.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recomendaciones")
public class RecomendacionController {

    private final RecomendacionService recomendacionService;
    private final UsuarioService usuarioService;

    public RecomendacionController(RecomendacionService recomendacionService,
                                   UsuarioService usuarioService) {
        this.recomendacionService = recomendacionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtener(Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        return ResponseEntity.ok(recomendacionService.recomendar(usuario));
    }
}