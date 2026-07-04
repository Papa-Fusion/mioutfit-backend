package com.outfitapp.backend.repository;

import com.outfitapp.backend.model.CalendarioOutfit;
import com.outfitapp.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarioRepository extends JpaRepository<CalendarioOutfit, Long> {
    List<CalendarioOutfit> findByUsuarioAndFechaBetween(Usuario usuario, LocalDate inicio, LocalDate fin);
    Optional<CalendarioOutfit> findByUsuarioAndFecha(Usuario usuario, LocalDate fecha);
}