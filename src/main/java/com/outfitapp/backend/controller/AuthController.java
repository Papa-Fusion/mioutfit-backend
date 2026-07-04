package com.outfitapp.backend.controller;

import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.service.JwtService;
import com.outfitapp.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UsuarioService usuarioService,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        Usuario nuevo = usuarioService.registrar(usuario);
        return ResponseEntity.ok(Map.of(
            "mensaje", "Usuario registrado correctamente",
            "email", nuevo.getEmail()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                credenciales.get("email"),
                credenciales.get("password")
            )
        );

        UserDetails usuario = usuarioService.loadUserByUsername(credenciales.get("email"));
        String token = jwtService.generarToken(usuario);

        return ResponseEntity.ok(Map.of("token", token));
    }
}