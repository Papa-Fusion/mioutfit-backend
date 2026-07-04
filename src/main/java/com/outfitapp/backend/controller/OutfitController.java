package com.outfitapp.backend.controller;

import com.outfitapp.backend.model.Outfit;
import com.outfitapp.backend.model.Prenda;
import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.repository.PrendaRepository;
import com.outfitapp.backend.service.OutfitService;
import com.outfitapp.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.outfitapp.backend.repository.OutfitRepository;
import com.outfitapp.backend.repository.PrendaRepository;
import com.outfitapp.backend.model.Prenda;
import com.outfitapp.backend.repository.CalendarioRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/outfits")
public class OutfitController {

	private final OutfitService outfitService;
	private final UsuarioService usuarioService;
	private final OutfitRepository outfitRepository;
	private final PrendaRepository prendaRepository;
	private final CalendarioRepository calendarioRepository;

	public OutfitController(OutfitService outfitService,
				            UsuarioService usuarioService,
				            OutfitRepository outfitRepository,
				            PrendaRepository prendaRepository,
				            CalendarioRepository calendarioRepository) {
		this.outfitService = outfitService;
		this.usuarioService = usuarioService;
		this.outfitRepository = outfitRepository;
		this.prendaRepository = prendaRepository;
		this.calendarioRepository = calendarioRepository;
	}

    @GetMapping
    public ResponseEntity<List<Outfit>> obtenerTodos(Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        return ResponseEntity.ok(outfitService.obtenerPorUsuario(usuario));
    }

    @PostMapping
    public ResponseEntity<Outfit> guardar(@RequestBody Map<String, Object> body,
                                          Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());

        Outfit outfit = new Outfit();
        outfit.setNombre((String) body.get("nombre"));
        outfit.setDescripcion((String) body.get("descripcion"));
        outfit.setUsuario(usuario);

        List<Integer> prendaIds = (List<Integer>) body.get("prendaIds");
        List<Prenda> prendas = prendaIds.stream()
                .map(id -> prendaRepository.findById(Long.valueOf(id)).orElseThrow())
                .toList();
        outfit.setPrendas(prendas);

        return ResponseEntity.ok(outfitService.guardar(outfit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Outfit> actualizar(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body,
                                              Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        Outfit outfit = outfitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outfit no encontrado"));

        if (!outfit.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        outfit.setNombre((String) body.get("nombre"));
        outfit.setDescripcion((String) body.get("descripcion"));

        List<Integer> prendaIds = (List<Integer>) body.get("prendaIds");
        if (prendaIds != null) {
            List<Prenda> prendas = prendaIds.stream()
                    .map(pid -> prendaRepository.findById(Long.valueOf(pid)).orElseThrow())
                    .toList();
            outfit.setPrendas(prendas);
        }

        return ResponseEntity.ok(outfitService.guardar(outfit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        Outfit outfit = outfitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outfit no encontrado"));

        if (!outfit.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        outfitService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/usar")
    public ResponseEntity<Outfit> registrarUso(@PathVariable Long id, Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        Outfit outfit = outfitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outfit no encontrado"));

        if (!outfit.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        outfit.setVecesUsado(outfit.getVecesUsado() + 1);
        outfit.setUltimoUso(java.time.LocalDate.now());

        // Suma el uso a cada prenda del outfit
        for (Prenda prenda : outfit.getPrendas()) {
            prenda.setVecesUsado(prenda.getVecesUsado() + 1);
            prenda.setUltimoUso(java.time.LocalDate.now());
            prendaRepository.save(prenda);
        }

        // Registra en el calendario
        java.time.LocalDate hoy = java.time.LocalDate.now();
        calendarioRepository.findByUsuarioAndFecha(usuario, hoy).ifPresentOrElse(
            registro -> {
                registro.setOutfit(outfit);
                calendarioRepository.save(registro);
            },
            () -> {
                com.outfitapp.backend.model.CalendarioOutfit registro =
                    new com.outfitapp.backend.model.CalendarioOutfit();
                registro.setUsuario(usuario);
                registro.setOutfit(outfit);
                registro.setFecha(hoy);
                registro.setNota("Marcado desde Mis outfits");
                calendarioRepository.save(registro);
            }
        );

        return ResponseEntity.ok(outfitService.guardar(outfit));
    }
}