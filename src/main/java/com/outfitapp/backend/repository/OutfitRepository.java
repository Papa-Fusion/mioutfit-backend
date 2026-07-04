package com.outfitapp.backend.repository;

import com.outfitapp.backend.model.Outfit;
import com.outfitapp.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    List<Outfit> findByUsuario(Usuario usuario);
}