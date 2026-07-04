package com.outfitapp.backend.controller;

import com.outfitapp.backend.model.CalendarioOutfit;
import com.outfitapp.backend.model.Outfit;
import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.repository.CalendarioRepository;
import com.outfitapp.backend.repository.OutfitRepository;
import com.outfitapp.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/calendario")
public class CalendarioController {

    private final CalendarioRepository calendarioRepository;
    private final OutfitRepository outfitRepository;
    private final UsuarioService usuarioService;

    public CalendarioController(CalendarioRepository calendarioRepository,
                                OutfitRepository outfitRepository,
                                UsuarioService usuarioService) {
        this.calendarioRepository = calendarioRepository;
        this.outfitRepository = outfitRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<CalendarioOutfit>> obtenerMes(
            @RequestParam int anio,
            @RequestParam int mes,
            Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());
        return ResponseEntity.ok(
            calendarioRepository.findByUsuarioAndFechaBetween(usuario, inicio, fin)
        );
    }

    @PostMapping
    public ResponseEntity<CalendarioOutfit> guardar(@RequestBody Map<String, Object> body,
                                                     Authentication auth) {
        Usuario usuario = (Usuario) usuarioService.loadUserByUsername(auth.getName());
        LocalDate fecha = LocalDate.parse((String) body.get("fecha"));

        // Si ya existe un registro para esa fecha lo reemplaza
        Optional<CalendarioOutfit> existente = calendarioRepository.findByUsuarioAndFecha(usuario, fecha);
        CalendarioOutfit registro = existente.orElse(new CalendarioOutfit());

        Long outfitId = Long.valueOf(body.get("outfitId").toString());
        Outfit outfit = outfitRepository.findById(outfitId).orElseThrow();

        registro.setUsuario(usuario);
        registro.setOutfit(outfit);
        registro.setFecha(fecha);
        registro.setNota(body.get("nota") != null ? (String) body.get("nota") : "");

        return ResponseEntity.ok(calendarioRepository.save(registro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        calendarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}