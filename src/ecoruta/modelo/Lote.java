package ecoruta.modelo;

/**
 * Clase abstracta que representa un lote de material reciclable en EcoRuta.
 * Cada lote pertenece a una sola entrega, tiene un código único dentro de esa entrega,
 * una descripción y un peso en kilogramos enteros positivos.
 */
public abstract class Lote {

    private final String codigo;
    private final String descripcion;
    private final int pesoKg;

    /**
     * Constructor principal de Lote.
     *
     * @param codigo Identificador único del lote dentro de la entrega.
     * @param descripcion Descripción del lote.
     * @param pesoKg Peso en kilogramos (debe ser un entero positivo > 0).
     */
    public Lote(String codigo, String descripcion, int pesoKg) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del lote no puede estar vacío.");
        }
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("El peso en kilogramos debe ser un entero positivo mayor a cero.");
        }
        this.codigo = codigo.trim();
        this.descripcion = (descripcion != null) ? descripcion.trim() : "";
        this.pesoKg = pesoKg;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPesoKg() {
        return pesoKg;
    }

    /**
     * Método abstracto para calcular el valor monetario reconocido por el lote.
     * Cada tipo de lote debe sobrescribir este método de forma que el valor
     * total de la entrega se calcule de forma polimórfica.
     *
     * @return Valor económico reconocido en pesos ($).
     */
    public abstract double calcularValor();

    @Override
    public String toString() {
        return String.format("Lote [código=%s, desc='%s', peso=%d kg, valor=$%,.2f]",
                codigo, descripcion, pesoKg, calcularValor());
    }
}
