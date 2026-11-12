package ecoruta.modelo;

/**
 * Representa un lote de Papel y Cartón en EcoRuta.
 * Registra si el material fue clasificado antes de entregarse.
 * Se reconocen $500 por kilogramo, más $100 por kilogramo si está clasificado.
 * El papel y cartón se procesa sin etiqueta (no implementa Etiquetable).
 */
public class LotePapelCarton extends Lote {

    public static final double TARIFA_BASE_KG = 500.0;
    public static final double BONIFICACION_CLASIFICADO_KG = 100.0;

    private final boolean clasificado;

    /**
     * Constructor para un lote de papel y cartón.
     *
     * @param codigo Código del lote único en la entrega.
     * @param descripcion Descripción del lote.
     * @param pesoKg Peso en kilogramos (entero positivo).
     * @param clasificado true si el material fue clasificado antes de entregarse; false de lo contrario.
     */
    public LotePapelCarton(String codigo, String descripcion, int pesoKg, boolean clasificado) {
        super(codigo, descripcion, pesoKg);
        this.clasificado = clasificado;
    }

    public boolean isClasificado() {
        return clasificado;
    }

    @Override
    public double calcularValor() {
        double tarifaPorKg = TARIFA_BASE_KG + (clasificado ? BONIFICACION_CLASIFICADO_KG : 0.0);
        return getPesoKg() * tarifaPorKg;
    }

    @Override
    public String toString() {
        return String.format("Papel y Cartón [código=%s, peso=%d kg, clasificado=%s, valor=$%,.2f]",
                getCodigo(), getPesoKg(), (clasificado ? "SÍ" : "NO"), calcularValor());
    }
}
