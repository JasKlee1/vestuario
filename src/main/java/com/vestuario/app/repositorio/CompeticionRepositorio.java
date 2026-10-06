package com.vestuario.app.repositorio;
 
import com.vestuario.app.modelo.Competicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface CompeticionRepositorio extends JpaRepository<Competicion, Long> {
 
    List<Competicion> findAllByOrderByFechaInicioAsc();
}
