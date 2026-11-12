package ecoruta.modelo;

/**
 * Interfaz que define el comportamiento para aquellos lotes
 * que deben generar una etiqueta para control interno.
 */
public interface Etiquetable {

    /**
     * Genera la etiqueta de control interno del lote en formato de texto.
     *
     * @return Cadena de texto con la información de la etiqueta.
     */
    String generarEtiqueta();
}
