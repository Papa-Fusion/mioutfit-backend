package com.outfitapp.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "outfits")
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
        name = "outfit_prendas",
        joinColumns = @JoinColumn(name = "outfit_id"),
        inverseJoinColumns = @JoinColumn(name = "prenda_id")
    )
    private List<Prenda> prendas;
    
    @Column(nullable = false)
    private Integer vecesUsado = 0;

    @Column
    private java.time.LocalDate ultimoUso;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Usuario getUsuario() { return usuario; }
    public List<Prenda> getPrendas() { return prendas; }
    public Integer getVecesUsado() { return vecesUsado; }
    public java.time.LocalDate getUltimoUso() { return ultimoUso; }

    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setPrendas(List<Prenda> prendas) { this.prendas = prendas; }
    public void setVecesUsado(Integer vecesUsado) { this.vecesUsado = vecesUsado; }
    public void setUltimoUso(java.time.LocalDate ultimoUso) { this.ultimoUso = ultimoUso; }
}