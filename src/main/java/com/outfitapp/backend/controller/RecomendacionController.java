package com.outfitapp.backend.controller;

import com.outfitapp.backend.model.Prenda;
import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.service.GeminiService;
import com.outfitapp.backend.service.PrendaService;
import com.outfitapp.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recomendaciones")
public class RecomendacionController {

    private final PrendaService prendaService;
    private final UsuarioService usuarioService;
    private final GeminiService geminiService;

    public RecomendacionController(PrendaService prendaService,
                                   UsuarioService usuarioService,
                                   GeminiService geminiService) {
        this.prendaService = prendaService;
        this.usuarioService = usuarioService;
        this.geminiService = geminiService;
    }

    @GetMapping("/combinar")
    public ResponseEntity<?> combinar(
            @RequestParam String clima,
            @RequestParam(required = false, defaultValue = "") String categoria,
            Authentication auth) {

        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        List<Prenda> todasLasPrendas = prendaService.obtenerPorUsuario(usuario);

        if (todasLasPrendas.isEmpty()) {
            return ResponseEntity.ok(Map.of("combinaciones", "[]"));
        }

        String listaPrendas = todasLasPrendas.stream()
                .map(p -> String.format(
                    "- ID:%d | Tipo:%s | Nombre:%s | Color:%s | Categoria:%s | ImagenUrl:%s",
                    p.getId(),
                    p.getTipo() != null ? p.getTipo() : "Sin tipo",
                    p.getNombre(),
                    p.getColor() != null ? p.getColor() : "Sin color",
                    p.getCategoria() != null ? p.getCategoria() : "Sin categoria",
                    p.getImagenUrl() != null ? p.getImagenUrl() : ""
                ))
                .collect(Collectors.joining("\n"));

        String reglaCategoria = categoria.isBlank() ? "" :
                "7. El estilo de TODAS las combinaciones debe ser exclusivamente: " + categoria +
                ". Prioriza prendas cuya categoría coincida con este estilo.\n";

        String prompt = String.format("""
                Eres un experto en moda y estilismo. El usuario tiene las siguientes prendas en su armario:
                
                %s
                
                El clima actual es: %s
                
                Tu tarea es sugerir exactamente 3 combinaciones de outfits usando SOLO las prendas de la lista.
                
                REGLAS ESTRICTAS — si no puedes cumplirlas, omite esa combinación:
                1. Cada combinación DEBE tener exactamente UNA prenda superior (Camiseta/Top, Camisa/Blusa, Suéter/Knitwear, Chaqueta/Abrigo).
                2. Cada combinación DEBE tener exactamente UNA prenda inferior (Pantalón/Jean, Short/Bermuda, Falda) O exactamente UNA prenda de cuerpo entero (Vestido, Enterizo/Jumpsuit). Nunca ambas.
                3. NUNCA combines dos prendas del mismo tipo (ej: dos camisetas, dos pantalones).
                4. Opcionalmente puedes agregar calzado (Zapatos/Calzado).
                5. Considera el clima para elegir prendas apropiadas.
                6. Combina los colores de forma armoniosa.
                %s
                Responde ÚNICAMENTE con un array JSON válido con este formato exacto, sin texto adicional, sin markdown, sin explicaciones:
                [
                  {
                    "nombre": "Nombre creativo del look",
                    "motivo": "Por qué combinan bien estos colores y es apropiado para el clima (máximo 20 palabras)",
                    "prendas": [
                      {"id": 1, "nombre": "Nombre prenda", "tipo": "Tipo", "color": "Color", "imagenUrl": "url"},
                      {"id": 2, "nombre": "Nombre prenda", "tipo": "Tipo", "color": "Color", "imagenUrl": "url"}
                    ]
                  }
                ]
                """, listaPrendas, clima, reglaCategoria);

        try {
            String respuesta = geminiService.sugerirCombinaciones(prompt);
            respuesta = respuesta.trim()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
            return ResponseEntity.ok(Map.of("combinaciones", respuesta));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "No se pudieron generar combinaciones"));
        }
    }
}