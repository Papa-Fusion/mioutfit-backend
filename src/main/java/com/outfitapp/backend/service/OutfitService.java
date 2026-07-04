package com.outfitapp.backend.service;

import com.outfitapp.backend.model.Outfit;
import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.repository.OutfitRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OutfitService {

    private final OutfitRepository outfitRepository;

    public OutfitService(OutfitRepository outfitRepository) {
        this.outfitRepository = outfitRepository;
    }

    public List<Outfit> obtenerPorUsuario(Usuario usuario) {
        return outfitRepository.findByUsuario(usuario);
    }

    public Outfit guardar(Outfit outfit) {
        return outfitRepository.save(outfit);
    }

    public void eliminar(Long id) {
        outfitRepository.deleteById(id);
    }
}