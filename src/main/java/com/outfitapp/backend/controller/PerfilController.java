package com.outfitapp.backend.controller;

import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.repository.UsuarioRepository;
import com.outfitapp.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public PerfilController(UsuarioService usuarioService,
                            UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<?> obtener(Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        return ResponseEntity.ok(Map.of(
            "id", usuario.getId(),
            "nombre", usuario.getNombre(),
            "email", usuario.getEmail(),
            "fotoUrl", usuario.getFotoUrl() != null ? usuario.getFotoUrl() : ""
        ));
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody Map<String, String> body,
                                         Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());

        if (body.containsKey("nombre")) {
            usuario.setNombre(body.get("nombre"));
        }
        if (body.containsKey("fotoUrl")) {
            usuario.setFotoUrl(body.get("fotoUrl"));
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of(
            "id", usuario.getId(),
            "nombre", usuario.getNombre(),
            "email", usuario.getEmail(),
            "fotoUrl", usuario.getFotoUrl() != null ? usuario.getFotoUrl() : ""
        ));
    }
}