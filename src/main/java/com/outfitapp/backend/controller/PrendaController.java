package com.outfitapp.backend.controller;

import com.outfitapp.backend.model.Prenda;
import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.service.PrendaService;
import com.outfitapp.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.outfitapp.backend.repository.PrendaRepository;
import java.util.List;

@RestController
@RequestMapping("/api/prendas")
public class PrendaController {

	private final PrendaService prendaService;
	private final UsuarioService usuarioService;
	private final PrendaRepository prendaRepository;

	public PrendaController(PrendaService prendaService,
	                        UsuarioService usuarioService,
	                        PrendaRepository prendaRepository) {
	    this.prendaService = prendaService;
	    this.usuarioService = usuarioService;
	    this.prendaRepository = prendaRepository;
	}

    @GetMapping
    public ResponseEntity<List<Prenda>> obtenerTodas(Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        return ResponseEntity.ok(prendaService.obtenerPorUsuario(usuario));
    }

    @PostMapping
    public ResponseEntity<Prenda> guardar(@RequestBody Prenda prenda, Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        prenda.setUsuario(usuario);
        return ResponseEntity.ok(prendaService.guardar(prenda));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Prenda> actualizar(@PathVariable Long id,
                                              @RequestBody Prenda prendaActualizada,
                                              Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        Prenda prenda = prendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));

        if (!prenda.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        prenda.setNombre(prendaActualizada.getNombre());
        prenda.setCategoria(prendaActualizada.getCategoria());
        prenda.setColor(prendaActualizada.getColor());
        prenda.setTalla(prendaActualizada.getTalla());
        prenda.setImagenUrl(prendaActualizada.getImagenUrl());

        return ResponseEntity.ok(prendaService.guardar(prenda));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        Prenda prenda = prendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));

        if (!prenda.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        prendaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/usar")
    public ResponseEntity<Prenda> registrarUso(@PathVariable Long id, Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        Prenda prenda = prendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));

        if (!prenda.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        prenda.setVecesUsado(prenda.getVecesUsado() + 1);
        prenda.setUltimoUso(java.time.LocalDate.now());

        return ResponseEntity.ok(prendaService.guardar(prenda));
    }
}