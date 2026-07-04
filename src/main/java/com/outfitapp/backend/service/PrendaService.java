package com.outfitapp.backend.service;

import com.outfitapp.backend.model.Prenda;
import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.repository.PrendaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PrendaService {

    private final PrendaRepository prendaRepository;

    public PrendaService(PrendaRepository prendaRepository) {
        this.prendaRepository = prendaRepository;
    }

    public List<Prenda> obtenerPorUsuario(Usuario usuario) {
        return prendaRepository.findByUsuario(usuario);
    }

    public Prenda guardar(Prenda prenda) {
        return prendaRepository.save(prenda);
    }

    public void eliminar(Long id) {
        prendaRepository.deleteById(id);
    }
}