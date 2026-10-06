package com.vestuario.app.modelo;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
 
/**
 * Jugador de la plantilla. La FK id_club la crea la relación @OneToMany
 * de Club mediante @JoinColumn; por eso esta clase no tiene atributo club.
 */
@Entity
@Table(name = "jugadores")
public class Jugador {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @NotBlank(message = "Escribe el nombre")
    @Size(max = 40, message = "Máximo 40 caracteres")
    @Column(nullable = false, length = 40)
    private String nombre;
 
    @NotBlank(message = "Escribe el apellido")
    @Size(max = 40, message = "Máximo 40 caracteres")
    @Column(nullable = false, length = 40)
    private String apellido;
 
    @NotNull(message = "Indica el dorsal")
    @Min(value = 1, message = "Dorsal entre 1 y 99")
    @Max(value = 99, message = "Dorsal entre 1 y 99")
    @Column(nullable = false)
    private Integer numero;
 
    @NotNull(message = "Elige la posición")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Posicion posicion;
 
    public Jugador() {
    }
 
    public Jugador(String nombre, String apellido, Integer numero, Posicion posicion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero = numero;
        this.posicion = posicion;
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
 
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
 
    public Posicion getPosicion() { return posicion; }
    public void setPosicion(Posicion posicion) { this.posicion = posicion; }
}
