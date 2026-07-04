package com.outfitapp.backend.service;

import com.outfitapp.backend.model.Prenda;
import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.repository.PrendaRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecomendacionService {

    private final PrendaRepository prendaRepository;

    public RecomendacionService(PrendaRepository prendaRepository) {
        this.prendaRepository = prendaRepository;
    }

    public List<Map<String, Object>> recomendar(Usuario usuario) {
        List<Prenda> prendas = prendaRepository.findByUsuario(usuario);
        List<Map<String, Object>> recomendaciones = new ArrayList<>();

        // Separar prendas por categoría
        Map<String, List<Prenda>> porCategoria = new HashMap<>();
        for (Prenda prenda : prendas) {
            porCategoria
                .computeIfAbsent(prenda.getCategoria(), k -> new ArrayList<>())
                .add(prenda);
        }

        // Combinar Casual con Casual
        generarCombinacion(porCategoria, recomendaciones,
            "Look casual", "Casual", "Casual");

        // Combinar Formal con Formal
        generarCombinacion(porCategoria, recomendaciones,
            "Look formal", "Formal", "Formal");

        // Combinar Casual con Deportivo
        generarCombinacion(porCategoria, recomendaciones,
            "Look sport casual", "Casual", "Deportivo");

        // Combinar Elegante con Formal
        generarCombinacion(porCategoria, recomendaciones,
            "Look elegante", "Elegante", "Formal");

        return recomendaciones;
    }

    private void generarCombinacion(
            Map<String, List<Prenda>> porCategoria,
            List<Map<String, Object>> recomendaciones,
            String nombreSugerencia,
            String categoria1,
            String categoria2) {

        List<Prenda> grupo1 = porCategoria.getOrDefault(categoria1, List.of());
        List<Prenda> grupo2 = porCategoria.getOrDefault(categoria2, List.of());

        if (grupo1.isEmpty()) return;

        // Si las categorías son iguales necesitamos al menos 2 prendas
        if (categoria1.equals(categoria2) && grupo1.size() < 2) return;

        Map<String, Object> sugerencia = new HashMap<>();
        sugerencia.put("nombre", nombreSugerencia);

        List<Map<String, Object>> prendasSugeridas = new ArrayList<>();

        if (categoria1.equals(categoria2)) {
            // Tomar dos prendas distintas de la misma categoría
            prendasSugeridas.add(prendaToMap(grupo1.get(0)));
            prendasSugeridas.add(prendaToMap(grupo1.get(1)));
        } else {
            // Tomar una de cada categoría
            prendasSugeridas.add(prendaToMap(grupo1.get(0)));
            if (!grupo2.isEmpty()) {
                prendasSugeridas.add(prendaToMap(grupo2.get(0)));
            }
        }

        sugerencia.put("prendas", prendasSugeridas);
        recomendaciones.add(sugerencia);
    }

    private Map<String, Object> prendaToMap(Prenda prenda) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", prenda.getId());
        map.put("nombre", prenda.getNombre());
        map.put("categoria", prenda.getCategoria());
        map.put("imagenUrl", prenda.getImagenUrl());
        return map;
    }
}