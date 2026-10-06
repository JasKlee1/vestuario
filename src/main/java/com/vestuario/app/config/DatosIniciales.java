package com.vestuario.app.config;
 
import com.vestuario.app.modelo.Asociacion;
import com.vestuario.app.modelo.Club;
import com.vestuario.app.modelo.Competicion;
import com.vestuario.app.modelo.Entrenador;
import com.vestuario.app.modelo.Jugador;
import com.vestuario.app.modelo.Posicion;
import com.vestuario.app.repositorio.AsociacionRepositorio;
import com.vestuario.app.repositorio.ClubRepositorio;
import com.vestuario.app.repositorio.CompeticionRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDate;
import java.util.List;
 
/**
 * H2 en memoria se borra en cada reinicio. Esta clase carga datos de ejemplo
 * al arrancar para que la aplicación no inicie vacía. Los nombres de entrenador
 * y jugadores son ficticios.
 */
@Component
public class DatosIniciales implements CommandLineRunner {
 
    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);
 
    private final AsociacionRepositorio asociacionRepositorio;
    private final CompeticionRepositorio competicionRepositorio;
    private final ClubRepositorio clubRepositorio;
 
    public DatosIniciales(AsociacionRepositorio asociacionRepositorio,
                          CompeticionRepositorio competicionRepositorio,
                          ClubRepositorio clubRepositorio) {
        this.asociacionRepositorio = asociacionRepositorio;
        this.competicionRepositorio = competicionRepositorio;
        this.clubRepositorio = clubRepositorio;
    }
 
    @Override
    @Transactional
    public void run(String... args) {
        if (asociacionRepositorio.count() > 0) {
            return;
        }
 
        Asociacion fcf = new Asociacion("Federación Colombiana de Fútbol", "FCF", "Colombia", null);
        List<Asociacion> asociaciones = asociacionRepositorio.saveAll(List.of(
                fcf,
                new Asociacion("Asociación del Fútbol Argentino", "AFA", "Argentina", null),
                new Asociacion("Confederación Brasileña de Fútbol", "CBF", "Brasil", null),
                new Asociacion("Federación Mexicana de Fútbol", "FMF", "México", null)
        ));
 
        Competicion libertadores = new Competicion("Copa Libertadores", 20_000_000L,
                LocalDate.of(2026, 2, 3), LocalDate.of(2026, 11, 28));
        Competicion liga = new Competicion("Liga nacional", 1_500_000L,
                LocalDate.of(2026, 1, 20), LocalDate.of(2026, 12, 15));
        competicionRepositorio.saveAll(List.of(
                libertadores,
                liga,
                new Competicion("Copa Sudamericana", 6_000_000L,
                        LocalDate.of(2026, 3, 3), LocalDate.of(2026, 11, 21))
        ));
 
        // Club de ejemplo (el PDF de la tarea usa Millonarios y la FCF como caso)
        Club club = new Club();
        club.setNombre("Millonarios");
        club.setPais("Colombia");
        club.setAnioFundacion(1946);
        club.setEntrenador(new Entrenador("Andrés", "Rojas", 48, "Colombiana"));
        club.setAsociacion(asociaciones.get(0));
        club.getCompeticiones().addAll(List.of(libertadores, liga));
        club.getJugadores().addAll(List.of(
                new Jugador("Camilo", "Torres", 1, Posicion.PORTERO),
                new Jugador("Julián", "Ortega", 4, Posicion.DEFENSA),
                new Jugador("Mateo", "Cárdenas", 8, Posicion.MEDIOCAMPISTA),
                new Jugador("Santiago", "Rincón", 10, Posicion.MEDIOCAMPISTA),
                new Jugador("Daniel", "Pardo", 9, Posicion.DELANTERO)
        ));
        clubRepositorio.save(club);
 
        log.info("Datos de ejemplo cargados en H2");
    }
}
