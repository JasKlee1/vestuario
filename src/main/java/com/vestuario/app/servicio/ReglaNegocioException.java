package com.vestuario.app.servicio;
 
/**
 * Excepción para reglas de negocio. Guarda el campo del formulario afectado
 * para que el controlador muestre el mensaje junto al input correcto.
 * Si el campo es null, el error se muestra como mensaje general.
 */
public class ReglaNegocioException extends RuntimeException {
 
    private final String campo;
 
    public ReglaNegocioException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }
 
    public String getCampo() {
        return campo;
    }
}
