package com.vestuario.app.modelo;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
/**
 * Asociación o federación. Relación N:1 desde Club: muchos clubes
 * pertenecen a una misma asociación (la FK asociacion_id está en "clubes").
 */
@Entity
@Table(name = "asociaciones")
public class Asociacion {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @NotBlank(message = "Escribe el nombre de la asociación")
    @Size(max = 80, message = "Máximo 80 caracteres")
    @Column(nullable = false, length = 80)
    private String nombre;
 
    @NotBlank(message = "Escribe las siglas")
    @Size(max = 10, message = "Máximo 10 caracteres")
    @Column(nullable = false, unique = true, length = 10)
    private String siglas;
 
    @NotBlank(message = "Indica el país")
    @Size(max = 40, message = "Máximo 40 caracteres")
    @Column(nullable = false, length = 40)
    private String pais;
 
    @Size(max = 60, message = "Máximo 60 caracteres")
    @Column(length = 60)
    private String presidente;
 
    public Asociacion() {
    }
 
    public Asociacion(String nombre, String siglas, String pais, String presidente) {
        this.nombre = nombre;
        this.siglas = siglas;
        this.pais = pais;
        this.presidente = presidente;
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public String getSiglas() { return siglas; }
    public void setSiglas(String siglas) { this.siglas = siglas; }
 
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
 
    public String getPresidente() { return presidente; }
    public void setPresidente(String presidente) { this.presidente = presidente; }
}
