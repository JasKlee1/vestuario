package com.vestuario.app.servicio;
 
import com.vestuario.app.modelo.Asociacion;
import com.vestuario.app.modelo.Club;
import com.vestuario.app.modelo.Competicion;
import com.vestuario.app.modelo.Entrenador;
import com.vestuario.app.modelo.Jugador;
import com.vestuario.app.repositorio.AsociacionRepositorio;
import com.vestuario.app.repositorio.ClubRepositorio;
import com.vestuario.app.repositorio.CompeticionRepositorio;
import com.vestuario.app.repositorio.JugadorRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
 
/**
 * Lógica de negocio de clubes y de su plantilla.
 * Todos los métodos se ejecutan dentro de una transacción.
 */
@Service
@Transactional
public class ClubServicio {
 
    public static final int MAXIMO_JUGADORES = 26;
 
    private final ClubRepositorio clubRepositorio;
    private final AsociacionRepositorio asociacionRepositorio;
    private final CompeticionRepositorio competicionRepositorio;
    private final JugadorRepositorio jugadorRepositorio;
 
    public ClubServicio(ClubRepositorio clubRepositorio,
                        AsociacionRepositorio asociacionRepositorio,
                        CompeticionRepositorio competicionRepositorio,
                        JugadorRepositorio jugadorRepositorio) {
        this.clubRepositorio = clubRepositorio;
        this.asociacionRepositorio = asociacionRepositorio;
        this.competicionRepositorio = competicionRepositorio;
        this.jugadorRepositorio = jugadorRepositorio;
    }
 
    // ---------- Consultas ----------
 
    @Transactional(readOnly = true)
    public List<Club> listar(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return clubRepositorio.findAllByOrderByNombreAsc();
        }
        return clubRepositorio.findByNombreContainingIgnoreCaseOrderByNombreAsc(filtro.trim());
    }
 
    @Transactional(readOnly = true)
    public Club buscarPorId(Long id) {
        return clubRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe un club con el código " + id));
    }
 
    @Transactional(readOnly = true)
    public long contarClubes() {
        return clubRepositorio.count();
    }
 
    @Transactional(readOnly = true)
    public long contarJugadores() {
        return jugadorRepositorio.count();
    }
 
    // ---------- Crear ----------
 
    public Club crear(Club club, Long asociacionId, List<Long> competicionIds) {
        club.setId(null);
        club.setNombre(club.getNombre().trim());
 
        if (clubRepositorio.existsByNombreIgnoreCase(club.getNombre())) {
            throw new ReglaNegocioException("nombre", "Ya existe un club llamado " + club.getNombre());
        }
        validarAnio(club.getAnioFundacion());
 
        club.getEntrenador().setId(null);
        club.setAsociacion(resolverAsociacion(asociacionId));
        club.setCompeticiones(resolverCompeticiones(competicionIds));
 
        // cascade = ALL en @OneToOne: el entrenador se inserta junto con el club.
        return clubRepositorio.save(club);
    }
 
    // ---------- Actualizar ----------
 
    /**
     * Copia los datos del formulario sobre el club existente.
     * La plantilla no se toca: se administra desde la ficha del club.
     */
    public Club actualizar(Long id, Club datos, Long asociacionId, List<Long> competicionIds) {
        Club club = buscarPorId(id);
        String nombre = datos.getNombre().trim();
 
        if (clubRepositorio.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("nombre", "Ya existe otro club llamado " + nombre);
        }
        validarAnio(datos.getAnioFundacion());
 
        club.setNombre(nombre);
        club.setPais(datos.getPais());
        club.setAnioFundacion(datos.getAnioFundacion());
 
        Entrenador entrenador = club.getEntrenador();
        Entrenador nuevo = datos.getEntrenador();
        entrenador.setNombre(nuevo.getNombre());
        entrenador.setApellido(nuevo.getApellido());
        entrenador.setEdad(nuevo.getEdad());
        entrenador.setNacionalidad(nuevo.getNacionalidad());
 
        club.setAsociacion(resolverAsociacion(asociacionId));
        club.getCompeticiones().clear();
        club.getCompeticiones().addAll(resolverCompeticiones(competicionIds));
 
        // La entidad está administrada: JPA guarda los cambios al cerrar la transacción.
        return club;
    }
 
    // ---------- Eliminar ----------
 
    /**
     * Elimina el club. Por cascade + orphanRemoval también se eliminan
     * su entrenador y sus jugadores, y las filas de clubes_competiciones.
     * La asociación y las competiciones se conservan.
     */
    public void eliminar(Long id) {
        clubRepositorio.delete(buscarPorId(id));
    }
 
    // ---------- Plantilla ----------
 
    public Club agregarJugador(Long clubId, Jugador jugador) {
        Club club = buscarPorId(clubId);
 
        if (club.getJugadores().size() >= MAXIMO_JUGADORES) {
            throw new ReglaNegocioException("numero",
                    "La plantilla ya tiene " + MAXIMO_JUGADORES + " jugadores");
        }
        boolean dorsalOcupado = club.getJugadores().stream()
                .anyMatch(j -> j.getNumero().equals(jugador.getNumero()));
        if (dorsalOcupado) {
            throw new ReglaNegocioException("numero",
                    "El dorsal " + jugador.getNumero() + " ya está asignado en este club");
        }
 
        jugador.setId(null);
        club.getJugadores().add(jugador);
        return club;
    }
 
    public void quitarJugador(Long clubId, Long jugadorId) {
        Club club = buscarPorId(clubId);
        boolean eliminado = club.getJugadores().removeIf(j -> j.getId().equals(jugadorId));
        if (!eliminado) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El jugador no pertenece a este club");
        }
        // orphanRemoval = true: al sacarlo de la lista, JPA lo borra de la tabla.
    }
 
    // ---------- Reglas internas ----------
 
    private void validarAnio(Integer anio) {
        int actual = Year.now().getValue();
        if (anio != null && anio > actual) {
            throw new ReglaNegocioException("anioFundacion",
                    "El año no puede ser posterior a " + actual);
        }
    }
 
    private Asociacion resolverAsociacion(Long asociacionId) {
        if (asociacionId == null) {
            throw new ReglaNegocioException("asociacion", "Selecciona la asociación del club");
        }
        return asociacionRepositorio.findById(asociacionId)
                .orElseThrow(() -> new ReglaNegocioException("asociacion",
                        "La asociación seleccionada no existe"));
    }
 
    private List<Competicion> resolverCompeticiones(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<Competicion> encontradas = competicionRepositorio.findAllById(ids);
        if (encontradas.size() != new HashSet<>(ids).size()) {
            throw new ReglaNegocioException("competiciones",
                    "Alguna competición seleccionada no existe");
        }
        return new ArrayList<>(encontradas);
    }
}
