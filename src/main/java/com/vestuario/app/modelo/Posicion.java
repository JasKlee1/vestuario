package com.vestuario.app.modelo;
 
/**
 * Posiciones de juego. Se almacena como texto (EnumType.STRING)
 * para que la columna sea legible en la consola H2.
 */
public enum Posicion {
 
    PORTERO("Portero"),
    DEFENSA("Defensa"),
    MEDIOCAMPISTA("Mediocampista"),
    DELANTERO("Delantero");
 
    private final String etiqueta;
 
    Posicion(String etiqueta) {
        this.etiqueta = etiqueta;
    }
 
    public String getEtiqueta() {
        return etiqueta;
    }
}
