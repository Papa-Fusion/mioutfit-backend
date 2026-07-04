package com.outfitapp.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "prendas")
public class Prenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String categoria;

    @Column
    private String imagenUrl;

    @Column
    private String color;

    @Column
    private String talla;
    
    @Column
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToMany(mappedBy = "prendas", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Outfit> outfits;
    
    @Column(nullable = false)
    private Integer vecesUsado = 0;

    @Column
    private java.time.LocalDate ultimoUso;
    
    @Column
    private String marca;

    public List<Outfit> getOutfits() { return outfits; }
    public void setOutfits(List<Outfit> outfits) { this.outfits = outfits; }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getImagenUrl() { return imagenUrl; }
    public String getColor() { return color; }
    public String getTalla() { return talla; }
    public Usuario getUsuario() { return usuario; }
    public String getTipo() { return tipo; }
    public Integer getVecesUsado() { return vecesUsado; }
    public java.time.LocalDate getUltimoUso() { return ultimoUso; }
    public String getMarca() { return marca; }

    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public void setColor(String color) { this.color = color; }
    public void setTalla(String talla) { this.talla = talla; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setVecesUsado(Integer vecesUsado) { this.vecesUsado = vecesUsado; }
    public void setUltimoUso(java.time.LocalDate ultimoUso) { this.ultimoUso = ultimoUso; }
    public void setMarca(String marca) { this.marca = marca; }
}