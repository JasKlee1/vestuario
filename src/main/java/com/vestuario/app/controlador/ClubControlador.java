package com.vestuario.app.controlador;
 
import com.vestuario.app.modelo.Club;
import com.vestuario.app.modelo.Competicion;
import com.vestuario.app.modelo.Jugador;
import com.vestuario.app.modelo.Posicion;
import com.vestuario.app.servicio.AsociacionServicio;
import com.vestuario.app.servicio.ClubServicio;
import com.vestuario.app.servicio.CompeticionServicio;
import com.vestuario.app.servicio.ReglaNegocioException;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import java.util.List;
 
/**
 * Controlador web de clubes: listado, ficha, alta, edición, eliminación
 * y administración de la plantilla de jugadores.
 */
@Controller
public class ClubControlador {
 
    private final ClubServicio clubServicio;
    private final AsociacionServicio asociacionServicio;
    private final CompeticionServicio competicionServicio;
 
    public ClubControlador(ClubServicio clubServicio,
                           AsociacionServicio asociacionServicio,
                           CompeticionServicio competicionServicio) {
        this.clubServicio = clubServicio;
        this.asociacionServicio = asociacionServicio;
        this.competicionServicio = competicionServicio;
    }
 
    /** Convierte textos vacíos en null y protege campos que el formulario no debe enviar. */
    @InitBinder("club")
    void configurarClub(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setDisallowedFields("id", "entrenador.id", "asociacion*", "competiciones*", "jugadores*");
    }
 
    @InitBinder("jugador")
    void configurarJugador(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setDisallowedFields("id");
    }
 
    // ---------- Listado ----------
 
    @GetMapping({"/", "/clubes"})
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("clubes", clubServicio.listar(q));
        model.addAttribute("q", q);
        model.addAttribute("totalClubes", clubServicio.contarClubes());
        model.addAttribute("totalJugadores", clubServicio.contarJugadores());
        model.addAttribute("totalAsociaciones", asociacionServicio.listar().size());
        model.addAttribute("totalCompeticiones", competicionServicio.listar().size());
        return "clubes/lista";
    }
 
    // ---------- Ficha del club ----------
 
    @GetMapping("/clubes/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("club", clubServicio.buscarPorId(id));
        model.addAttribute("jugador", new Jugador());
        model.addAttribute("posiciones", Posicion.values());
        return "clubes/detalle";
    }
 
    // ---------- Alta ----------
 
    @GetMapping("/clubes/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("club", new Club());
        cargarFormulario(model, null, null);
        return "clubes/formulario";
    }
 
    @PostMapping("/clubes")
    public String crear(@Valid @ModelAttribute("club") Club club,
                        BindingResult resultado,
                        @RequestParam(required = false) Long asociacionId,
                        @RequestParam(required = false) List<Long> competicionIds,
                        Model model,
                        RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                Club creado = clubServicio.crear(club, asociacionId, competicionIds);
                redirect.addFlashAttribute("exito", "Club " + creado.getNombre() + " registrado.");
                return "redirect:/clubes/" + creado.getId();
            } catch (ReglaNegocioException e) {
                registrarError(resultado, e);
            }
        }
        cargarFormulario(model, asociacionId, competicionIds);
        return "clubes/formulario";
    }
 
    // ---------- Edición ----------
 
    @GetMapping("/clubes/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Club club = clubServicio.buscarPorId(id);
        model.addAttribute("club", club);
        Long asociacionId = club.getAsociacion() == null ? null : club.getAsociacion().getId();
        List<Long> competicionIds = club.getCompeticiones().stream().map(Competicion::getId).toList();
        cargarFormulario(model, asociacionId, competicionIds);
        return "clubes/formulario";
    }
 
    @PostMapping("/clubes/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("club") Club datos,
                             BindingResult resultado,
                             @RequestParam(required = false) Long asociacionId,
                             @RequestParam(required = false) List<Long> competicionIds,
                             Model model,
                             RedirectAttributes redirect) {
        datos.setId(id); // necesario para que el formulario conserve la ruta de edición
        if (!resultado.hasErrors()) {
            try {
                clubServicio.actualizar(id, datos, asociacionId, competicionIds);
                redirect.addFlashAttribute("exito", "Cambios guardados.");
                return "redirect:/clubes/" + id;
            } catch (ReglaNegocioException e) {
                registrarError(resultado, e);
            }
        }
        cargarFormulario(model, asociacionId, competicionIds);
        return "clubes/formulario";
    }
 
    // ---------- Eliminación ----------
 
    @PostMapping("/clubes/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        String nombre = clubServicio.buscarPorId(id).getNombre();
        clubServicio.eliminar(id);
        redirect.addFlashAttribute("exito", "Club " + nombre + " eliminado junto con su entrenador y su plantilla.");
        return "redirect:/clubes";
    }
 
    // ---------- Plantilla ----------
 
    @PostMapping("/clubes/{id}/jugadores")
    public String agregarJugador(@PathVariable Long id,
                                 @Valid @ModelAttribute("jugador") Jugador jugador,
                                 BindingResult resultado,
                                 Model model,
                                 RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                clubServicio.agregarJugador(id, jugador);
                redirect.addFlashAttribute("exito",
                        jugador.getNombre() + " " + jugador.getApellido() + " agregado a la plantilla.");
                return "redirect:/clubes/" + id;
            } catch (ReglaNegocioException e) {
                registrarError(resultado, e);
            }
        }
        model.addAttribute("club", clubServicio.buscarPorId(id));
        model.addAttribute("posiciones", Posicion.values());
        return "clubes/detalle";
    }
 
    @PostMapping("/clubes/{id}/jugadores/{jugadorId}/eliminar")
    public String quitarJugador(@PathVariable Long id,
                                @PathVariable Long jugadorId,
                                RedirectAttributes redirect) {
        clubServicio.quitarJugador(id, jugadorId);
        redirect.addFlashAttribute("exito", "Jugador retirado de la plantilla.");
        return "redirect:/clubes/" + id;
    }
 
    // ---------- Apoyo ----------
 
    private void cargarFormulario(Model model, Long asociacionId, List<Long> competicionIds) {
        model.addAttribute("asociaciones", asociacionServicio.listar());
        model.addAttribute("competiciones", competicionServicio.listar());
        model.addAttribute("asociacionId", asociacionId);
        model.addAttribute("competicionIds", competicionIds == null ? List.of() : competicionIds);
    }
 
    private void registrarError(BindingResult resultado, ReglaNegocioException e) {
        if (e.getCampo() == null) {
            resultado.reject("negocio", e.getMessage());
        } else {
            resultado.rejectValue(e.getCampo(), "negocio", e.getMessage());
        }
    }
}
