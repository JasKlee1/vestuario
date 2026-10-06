package com.vestuario.app.repositorio;
 
import com.vestuario.app.modelo.Club;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface ClubRepositorio extends JpaRepository<Club, Long> {
 
    /*
     * @EntityGraph trae asociación, entrenador y jugadores en la misma consulta.
     * Así el listado evita el problema N + 1 aunque esas relaciones sean LAZY.
     */
    @EntityGraph(attributePaths = {"asociacion", "entrenador", "jugadores"})
    List<Club> findAllByOrderByNombreAsc();
 
    @EntityGraph(attributePaths = {"asociacion", "entrenador", "jugadores"})
    List<Club> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
 
    boolean existsByNombreIgnoreCase(String nombre);
 
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
 
    long countByAsociacion_Id(Long asociacionId);
 
    long countByCompeticiones_Id(Long competicionId);
 
    List<Club> findByCompeticiones_Id(Long competicionId);
}
