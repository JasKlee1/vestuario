package com.vestuario.app.repositorio;
 
import com.vestuario.app.modelo.Asociacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface AsociacionRepositorio extends JpaRepository<Asociacion, Long> {
 
    List<Asociacion> findAllByOrderByNombreAsc();
 
    boolean existsBySiglasIgnoreCase(String siglas);
}
