package com.vestuario.app.controlador;
 
import com.vestuario.app.modelo.Competicion;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
@Controller
@RequestMapping("/competiciones")
public class CompeticionControlador {
 
    private final CompeticionServicio competicionServicio;
 
    public CompeticionControlador(CompeticionServicio competicionServicio) {
        this.competicionServicio = competicionServicio;
    }
 
    @InitBinder("competicion")
    void configurar(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setDisallowedFields("id");
    }
 
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("competicion", new Competicion());
        cargarListado(model);
        return "competiciones/lista";
    }
 
    @PostMapping
    public String guardar(@Valid @ModelAttribute("competicion") Competicion competicion,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                Competicion guardada = competicionServicio.guardar(competicion);
                redirect.addFlashAttribute("exito", "Competición " + guardada.getNombre() + " registrada.");
                return "redirect:/competiciones";
            } catch (ReglaNegocioException e) {
                resultado.rejectValue(e.getCampo(), "negocio", e.getMessage());
            }
        }
        cargarListado(model);
        return "competiciones/lista";
    }
 
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        competicionServicio.eliminar(id);
        redirect.addFlashAttribute("exito", "Competición eliminada y retirada de los clubes inscritos.");
        return "redirect:/competiciones";
    }
 
    private void cargarListado(Model model) {
        model.addAttribute("competiciones", competicionServicio.listar());
        model.addAttribute("clubesPorCompeticion", competicionServicio.clubesPorCompeticion());
    }
}
