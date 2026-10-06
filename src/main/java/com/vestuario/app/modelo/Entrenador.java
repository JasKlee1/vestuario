package com.vestuario.app.modelo;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Entrenador de un club. Relación 1:1 con Club; el Club es el propietario
 * de la relación (tiene la FK entrenador_id), por eso esta clase no conoce al Club.
 */
@Entity
@Table(name = "entrenadores")
public class Entrenador {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @NotBlank(message = "Escribe el nombre del entrenador")
    @Size(max = 40, message = "Máximo 40 caracteres")
    @Column(nullable = false, length = 40)
    private String nombre;
 
    @NotBlank(message = "Escribe el apellido del entrenador")
    @Size(max = 40, message = "Máximo 40 caracteres")
    @Column(nullable = false, length = 40)
    private String apellido;
 
    @NotNull(message = "Indica la edad")
    @Min(value = 18, message = "La edad mínima es 18 años")
    @Max(value = 90, message = "La edad máxima es 90 años")
    private Integer edad;
 
    @NotBlank(message = "Indica la nacionalidad")
    @Size(max = 40, message = "Máximo 40 caracteres")
    @Column(nullable = false, length = 40)
    private String nacionalidad;
 
    public Entrenador() {
    }
 
    public Entrenador(String nombre, String apellido, Integer edad, String nacionalidad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.nacionalidad = nacionalidad;
    }
 
    /** Nombre y apellido juntos, para mostrar en las vistas. */
    public String getNombreCompleto() {
        return (nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido);
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
 
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
 
    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
}
