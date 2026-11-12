package ecoruta.modelo;

/**
 * Representa un lote de Residuos Electrónicos en EcoRuta.
 * Registra cuántos equipos completos contiene el lote (entero mayor o igual a cero).
 * Se reconocen $1.400 por kilogramo, más $800 por cada equipo completo.
 * Implementa la interfaz Etiquetable para generar su etiqueta de control interno,
 * la cual contiene el código del lote y la cantidad de equipos completos.
 */
public class LoteElectronico extends Lote implements Etiquetable {

    public static final double TARIFA_KG = 1400.0;
    public static final double TARIFA_EQUIPO_COMPLETO = 800.0;

    private final int equiposCompletos;

    /**
     * Constructor para un lote de residuos electrónicos.
     *
     * @param codigo Código del lote único en la entrega.
     * @param descripcion Descripción del lote.
     * @param pesoKg Peso en kilogramos (entero positivo).
     * @param equiposCompletos Cantidad de equipos completos (entero >= 0).
     */
    public LoteElectronico(String codigo, String descripcion, int pesoKg, int equiposCompletos) {
        super(codigo, descripcion, pesoKg);
        if (equiposCompletos < 0) {
            throw new IllegalArgumentException("La cantidad de equipos completos debe ser mayor o igual a cero.");
        }
        this.equiposCompletos = equiposCompletos;
    }

    public int getEquiposCompletos() {
        return equiposCompletos;
    }

    @Override
    public double calcularValor() {
        return (getPesoKg() * TARIFA_KG) + (equiposCompletos * TARIFA_EQUIPO_COMPLETO);
    }

    @Override
    public String generarEtiqueta() {
        return String.format("ETIQUETA_ELECTRONICO [Código: %s | Equipos completos: %d]", getCodigo(), equiposCompletos);
    }

    @Override
    public String toString() {
        return String.format("Residuo Electrónico [código=%s, equipos=%d, peso=%d kg, valor=$%,.2f]",
                getCodigo(), equiposCompletos, getPesoKg(), calcularValor());
    }
}
