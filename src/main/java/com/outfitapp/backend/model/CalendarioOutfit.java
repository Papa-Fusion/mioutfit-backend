package com.outfitapp.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "calendario_outfits")
public class CalendarioOutfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "outfit_id")
    private Outfit outfit;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column
    private String nota;

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Outfit getOutfit() { return outfit; }
    public LocalDate getFecha() { return fecha; }
    public String getNota() { return nota; }

    public void setId(Long id) { this.id = id; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setOutfit(Outfit outfit) { this.outfit = outfit; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setNota(String nota) { this.nota = nota; }
}