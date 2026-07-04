package com.outfitapp.backend.repository;

import com.outfitapp.backend.model.Prenda;
import com.outfitapp.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrendaRepository extends JpaRepository<Prenda, Long> {
    List<Prenda> findByUsuario(Usuario usuario);
}