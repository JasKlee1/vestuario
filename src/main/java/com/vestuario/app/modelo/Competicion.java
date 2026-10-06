package com.vestuario.app.modelo;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
 
import java.time.LocalDate;
 
/**
 * Competición. Relación N:N con Club a través de la tabla intermedia
 * "clubes_competiciones". Es unidireccional: solo Club conoce sus competiciones.
 */
@Entity
@Table(name = "competiciones")
public class Competicion {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @NotBlank(message = "Escribe el nombre de la competición")
    @Size(max = 60, message = "Máximo 60 caracteres")
    @Column(nullable = false, length = 60)
    private String nombre;
 
    @NotNull(message = "Indica el monto del premio")
    @PositiveOrZero(message = "El premio no puede ser negativo")
    private Long montoPremio;
 
    @NotNull(message = "Indica la fecha de inicio")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;
 
    @NotNull(message = "Indica la fecha de fin")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;
 
    public Competicion() {
    }
 
    public Competicion(String nombre, Long montoPremio, LocalDate fechaInicio, LocalDate fechaFin) {
        this.nombre = nombre;
        this.montoPremio = montoPremio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public Long getMontoPremio() { return montoPremio; }
    public void setMontoPremio(Long montoPremio) { this.montoPremio = montoPremio; }
 
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
 
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
