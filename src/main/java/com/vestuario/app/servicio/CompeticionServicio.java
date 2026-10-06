package com.vestuario.app.servicio;
 
import com.vestuario.app.modelo.Club;
import com.vestuario.app.modelo.Competicion;
import com.vestuario.app.repositorio.ClubRepositorio;
import com.vestuario.app.repositorio.CompeticionRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
 
@Service
@Transactional
public class CompeticionServicio {
 
    private final CompeticionRepositorio competicionRepositorio;
    private final ClubRepositorio clubRepositorio;
 
    public CompeticionServicio(CompeticionRepositorio competicionRepositorio,
                               ClubRepositorio clubRepositorio) {
        this.competicionRepositorio = competicionRepositorio;
        this.clubRepositorio = clubRepositorio;
    }
 
    @Transactional(readOnly = true)
    public List<Competicion> listar() {
        return competicionRepositorio.findAllByOrderByFechaInicioAsc();
    }
 
    /** Cantidad de clubes inscritos en cada competición (clave: id de la competición). */
    @Transactional(readOnly = true)
    public Map<Long, Long> clubesPorCompeticion() {
        Map<Long, Long> conteo = new LinkedHashMap<>();
        for (Competicion competicion : listar()) {
            conteo.put(competicion.getId(), clubRepositorio.countByCompeticiones_Id(competicion.getId()));
        }
        return conteo;
    }
 
    public Competicion guardar(Competicion competicion) {
        competicion.setId(null);
        if (competicion.getFechaFin().isBefore(competicion.getFechaInicio())) {
            throw new ReglaNegocioException("fechaFin",
                    "La fecha de fin debe ser igual o posterior a la de inicio");
        }
        return competicionRepositorio.save(competicion);
    }
 
    /**
     * Antes de borrar la competición se retira de los clubes inscritos;
     * así se eliminan sus filas en la tabla intermedia clubes_competiciones.
     */
    public void eliminar(Long id) {
        Competicion competicion = competicionRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe la competición " + id));
 
        for (Club club : clubRepositorio.findByCompeticiones_Id(id)) {
            club.getCompeticiones().removeIf(c -> c.getId().equals(id));
        }
        competicionRepositorio.delete(competicion);
    }
}
