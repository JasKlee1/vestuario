package com.vestuario.app.servicio;
 
import com.vestuario.app.modelo.Asociacion;
import com.vestuario.app.repositorio.AsociacionRepositorio;
import com.vestuario.app.repositorio.ClubRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
 
@Service
@Transactional
public class AsociacionServicio {
 
    private final AsociacionRepositorio asociacionRepositorio;
    private final ClubRepositorio clubRepositorio;
 
    public AsociacionServicio(AsociacionRepositorio asociacionRepositorio,
                              ClubRepositorio clubRepositorio) {
        this.asociacionRepositorio = asociacionRepositorio;
        this.clubRepositorio = clubRepositorio;
    }
 
    @Transactional(readOnly = true)
    public List<Asociacion> listar() {
        return asociacionRepositorio.findAllByOrderByNombreAsc();
    }
 
    /** Cantidad de clubes afiliados a cada asociación (clave: id de la asociación). */
    @Transactional(readOnly = true)
    public Map<Long, Long> clubesPorAsociacion() {
        Map<Long, Long> conteo = new LinkedHashMap<>();
        for (Asociacion asociacion : listar()) {
            conteo.put(asociacion.getId(), clubRepositorio.countByAsociacion_Id(asociacion.getId()));
        }
        return conteo;
    }
 
    public Asociacion guardar(Asociacion asociacion) {
        asociacion.setId(null);
        asociacion.setSiglas(asociacion.getSiglas().trim().toUpperCase());
 
        if (asociacionRepositorio.existsBySiglasIgnoreCase(asociacion.getSiglas())) {
            throw new ReglaNegocioException("siglas",
                    "Ya existe una asociación con las siglas " + asociacion.getSiglas());
        }
        return asociacionRepositorio.save(asociacion);
    }
 
    /**
     * Restricción de llave foránea: no se elimina una asociación
     * mientras tenga clubes afiliados (equivale a ON DELETE NO ACTION).
     */
    public void eliminar(Long id) {
        Asociacion asociacion = asociacionRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe la asociación " + id));
 
        long afiliados = clubRepositorio.countByAsociacion_Id(id);
        if (afiliados > 0) {
            throw new ReglaNegocioException(null,
                    "No se puede eliminar " + asociacion.getSiglas() + ": tiene " + afiliados
                            + (afiliados == 1 ? " club afiliado" : " clubes afiliados")
                            + ". Cambia la asociación de esos clubes primero.");
        }
        asociacionRepositorio.delete(asociacion);
    }
}
