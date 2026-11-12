package ecoruta.modelo;

/**
 * Representa un lote de Metales en EcoRuta.
 * Registra si el metal es ALUMINIO o ACERO.
 * Se reconocen $2.200 por kilogramo de aluminio y $900 por kilogramo de acero.
 * Implementa la interfaz Etiquetable para generar su etiqueta de control interno,
 * la cual contiene el código del lote y el tipo de metal.
 */
public class LoteMetal extends Lote implements Etiquetable {

    public static final double TARIFA_ALUMINIO_KG = 2200.0;
    public static final double TARIFA_ACERO_KG = 900.0;

    private final TipoMetal tipoMetal;

    /**
     * Constructor para un lote de metal.
     *
     * @param codigo Código del lote único en la entrega.
     * @param descripcion Descripción del lote.
     * @param pesoKg Peso en kilogramos (entero positivo).
     * @param tipoMetal Tipo de metal: ALUMINIO o ACERO.
     */
    public LoteMetal(String codigo, String descripcion, int pesoKg, TipoMetal tipoMetal) {
        super(codigo, descripcion, pesoKg);
        if (tipoMetal == null) {
            throw new IllegalArgumentException("El tipo de metal no puede ser nulo.");
        }
        this.tipoMetal = tipoMetal;
    }

    public TipoMetal getTipoMetal() {
        return tipoMetal;
    }

    @Override
    public double calcularValor() {
        double tarifaPorKg = (tipoMetal == TipoMetal.ALUMINIO) ? TARIFA_ALUMINIO_KG : TARIFA_ACERO_KG;
        return getPesoKg() * tarifaPorKg;
    }

    @Override
    public String generarEtiqueta() {
        return String.format("ETIQUETA_METAL [Código: %s | Tipo: %s]", getCodigo(), tipoMetal.name());
    }

    @Override
    public String toString() {
        return String.format("Metal [código=%s, tipo=%s, peso=%d kg, valor=$%,.2f]",
                getCodigo(), tipoMetal, getPesoKg(), calcularValor());
    }
}
