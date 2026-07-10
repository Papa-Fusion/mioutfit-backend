package com.outfitapp.backend.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.service.JwtService;
import com.outfitapp.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${google.client.id}")
    private String googleClientId;

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

    @PostMapping("/google")
    public ResponseEntity<?> loginConGoogle(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("idToken");

        try {
            // Verificar el token con Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token de Google inválido"));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String nombre = (String) payload.get("name");
            String fotoUrl = (String) payload.get("picture");

            // Buscar o crear el usuario (unifica cuentas por email)
            Usuario usuario = usuarioService.buscarOCrearUsuarioGoogle(email, nombre, fotoUrl);

            // Generar nuestro propio JWT
            String token = jwtService.generarToken(usuario);

            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al verificar token de Google"));
        }
    }
}