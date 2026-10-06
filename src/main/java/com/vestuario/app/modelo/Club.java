package com.vestuario.app.modelo;
 
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
 
/**
 * Entidad central del dominio. Concentra las cuatro relaciones JPA:
 *
 *  @OneToOne   Club -> Entrenador    (FK entrenador_id en "clubes")
 *  @OneToMany  Club -> Jugador       (FK id_club en "jugadores", sin tabla intermedia)
 *  @ManyToOne  Club -> Asociacion    (FK asociacion_id en "clubes")
 *  @ManyToMany Club -> Competicion   (tabla intermedia "clubes_competiciones")
 */
@Entity
@Table(name = "clubes")
public class Club {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @NotBlank(message = "Escribe el nombre del club")
    @Size(max = 60, message = "Máximo 60 caracteres")
    @Column(nullable = false, unique = true, length = 60)
    private String nombre;
 
    @NotBlank(message = "Indica el país del club")
    @Size(max = 40, message = "Máximo 40 caracteres")
    @Column(nullable = false, length = 40)
    private String pais;
 
    @NotNull(message = "Indica el año de fundación")
    @Min(value = 1850, message = "El año debe ser 1850 o posterior")
    private Integer anioFundacion;
 
    /*
     * 1:1 — Carga EAGER (valor por defecto de @OneToOne).
     * CascadeType.ALL + orphanRemoval: el entrenador se guarda, actualiza
     * y elimina junto con su club.
     */
    @Valid
    @NotNull(message = "El club necesita un entrenador")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "entrenador_id", unique = true)
    private Entrenador entrenador = new Entrenador();
 
    /*
     * 1:N — Carga LAZY (valor por defecto de @OneToMany).
     * @JoinColumn evita la tabla intermedia "clubes_jugadores":
     * la FK id_club queda directamente en la tabla "jugadores".
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_club")
    @OrderBy("numero ASC")
    private List<Jugador> jugadores = new ArrayList<>();
 
    /*
     * N:1 — Se cambia a LAZY (por defecto sería EAGER), siguiendo la
     * recomendación de no obtener datos que no se necesitan.
     * Sin cascade: borrar un club nunca borra su asociación.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asociacion_id")
    private Asociacion asociacion;
 
    /*
     * N:N — Carga LAZY (valor por defecto de @ManyToMany).
     * Relación unidireccional: Club es el propietario de la tabla intermedia.
     */
    @ManyToMany
    @JoinTable(
            name = "clubes_competiciones",
            joinColumns = @JoinColumn(name = "club_id"),
            inverseJoinColumns = @JoinColumn(name = "competicion_id"))
    private List<Competicion> competiciones = new ArrayList<>();
 
    public Club() {
    }
 
    // ---------- Métodos de apoyo para las vistas (no se persisten) ----------
 
    /** Iniciales del club para el escudo, por ejemplo "AN" para Atlético Nacional. */
    public String getIniciales() {
        if (nombre == null || nombre.isBlank()) {
            return "?";
        }
        return Arrays.stream(nombre.trim().split("\\s+"))
                .limit(2)
                .map(palabra -> palabra.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());
    }
 
    /** Cantidad de jugadores inscritos. */
    public int getTotalJugadores() {
        return jugadores == null ? 0 : jugadores.size();
    }
 
    // ---------- Getters y setters ----------
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
 
    public Integer getAnioFundacion() { return anioFundacion; }
    public void setAnioFundacion(Integer anioFundacion) { this.anioFundacion = anioFundacion; }
 
    public Entrenador getEntrenador() { return entrenador; }
    public void setEntrenador(Entrenador entrenador) { this.entrenador = entrenador; }
 
    public List<Jugador> getJugadores() { return jugadores; }
    public void setJugadores(List<Jugador> jugadores) { this.jugadores = jugadores; }
 
    public Asociacion getAsociacion() { return asociacion; }
    public void setAsociacion(Asociacion asociacion) { this.asociacion = asociacion; }
 
    public List<Competicion> getCompeticiones() { return competiciones; }
    public void setCompeticiones(List<Competicion> competiciones) { this.competiciones = competiciones; }
}
