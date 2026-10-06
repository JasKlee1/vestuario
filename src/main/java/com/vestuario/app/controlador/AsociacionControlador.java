package com.vestuario.app.controlador;
 
import com.vestuario.app.modelo.Asociacion;
import com.vestuario.app.servicio.AsociacionServicio;
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
@RequestMapping("/asociaciones")
public class AsociacionControlador {
 
    private final AsociacionServicio asociacionServicio;
 
    public AsociacionControlador(AsociacionServicio asociacionServicio) {
        this.asociacionServicio = asociacionServicio;
    }
 
    @InitBinder("asociacion")
    void configurar(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setDisallowedFields("id");
    }
 
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("asociacion", new Asociacion());
        cargarListado(model);
        return "asociaciones/lista";
    }
 
    @PostMapping
    public String guardar(@Valid @ModelAttribute("asociacion") Asociacion asociacion,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                Asociacion guardada = asociacionServicio.guardar(asociacion);
                redirect.addFlashAttribute("exito", "Asociación " + guardada.getSiglas() + " registrada.");
                return "redirect:/asociaciones";
            } catch (ReglaNegocioException e) {
                resultado.rejectValue(e.getCampo(), "negocio", e.getMessage());
            }
        }
        cargarListado(model);
        return "asociaciones/lista";
    }
 
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            asociacionServicio.eliminar(id);
            redirect.addFlashAttribute("exito", "Asociación eliminada.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asociaciones";
    }
 
    private void cargarListado(Model model) {
        model.addAttribute("asociaciones", asociacionServicio.listar());
        model.addAttribute("clubesPorAsociacion", asociacionServicio.clubesPorAsociacion());
    }
}
