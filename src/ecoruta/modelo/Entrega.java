package ecoruta.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una entrega de materiales reciclables en EcoRuta.
 * Contiene código único, fecha, aportante, lista de lotes, estado y comprobante.
 */
public class Entrega {

    private final String codigo;
    private final LocalDate fecha;
    private final Aportante aportante;
    private final List<Lote> lotes;
    private EstadoEntrega estado;
    private Comprobante comprobante;

    /**
     * Crea una nueva entrega en estado ABIERTA, sin lotes y sin comprobante.
     *
     * @param codigo Identificador único de la entrega.
     * @param fecha Fecha de registro de la entrega.
     * @param aportante Aportante registrado asociado a la entrega.
     */
    public Entrega(String codigo, LocalDate fecha, Aportante aportante) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de entrega no puede estar vacío.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }
        if (aportante == null) {
            throw new IllegalArgumentException("El aportante no puede ser nulo.");
        }
        this.codigo = codigo.trim();
        this.fecha = fecha;
        this.aportante = aportante;
        this.lotes = new ArrayList<>();
        this.estado = EstadoEntrega.ABIERTA;
        this.comprobante = null;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Aportante getAportante() {
        return aportante;
    }

    public EstadoEntrega getEstado() {
        return estado;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public List<Lote> getLotes() {
        return Collections.unmodifiableList(lotes);
    }

    /**
     * Agrega un lote a la entrega abierta.
     * Rechaza la operación si la entrega ya no está abierta, si el lote es nulo,
     * si el peso es menor o igual a 0, o si el código ya existe en esta entrega.
     *
     * @param lote Lote a agregar.
     * @return true si el lote fue agregado; false si fue rechazado.
     */
    public boolean agregarLote(Lote lote) {
        if (this.estado != EstadoEntrega.ABIERTA) {
            return false;
        }
        if (lote == null) {
            return false;
        }
        if (lote.getPesoKg() <= 0) {
            return false;
        }
        // Validar código sin repetir dentro de esta entrega
        for (Lote existente : lotes) {
            if (existente.getCodigo().equalsIgnoreCase(lote.getCodigo())) {
                return false;
            }
        }
        lotes.add(lote);
        return true;
    }

    /**
     * Calcula la suma total de los pesos de todos los lotes de la entrega.
     *
     * @return Peso total en kilogramos.
     */
    public int calcularPesoTotal() {
        int total = 0;
        for (Lote lote : lotes) {
            total += lote.getPesoKg();
        }
        return total;
    }

    /**
     * Calcula de forma polimórfica el valor total reconocido por todos los lotes de la entrega.
     * Invoca Lote.calcularValor() para cada tipo concreto de lote.
     *
     * @return Valor total económico en pesos.
     */
    public double calcularValorTotal() {
        double total = 0.0;
        for (Lote lote : lotes) {
            total += lote.calcularValor(); // Polimorfismo
        }
        return total;
    }

    /**
     * Acepta la entrega si está en estado ABIERTA y contiene al menos un lote.
     * Al aceptarla:
     * - Calcula de forma polimórfica el peso total y el valor total de sus lotes.
     * - Genera el comprobante inmutable con los datos exigidos.
     * - Cambia el estado a ACEPTADA.
     * - Queda cerrada a nuevos lotes.
     *
     * Si está vacía o ya fue aceptada, rechaza la operación y conserva la información.
     *
     * @return true si la aceptación fue exitosa; false si fue rechazada.
     */
    public boolean aceptar() {
        if (this.estado != EstadoEntrega.ABIERTA) {
            return false; // Ya fue aceptada o estado no válido
        }
        if (this.lotes.isEmpty()) {
            return false; // Entrega vacía rechazada
        }

        int pesoTotal = calcularPesoTotal();
        double valorTotal = calcularValorTotal();

        this.estado = EstadoEntrega.ACEPTADA;
        this.comprobante = new Comprobante(this.codigo, this.aportante.identificacion(), pesoTotal, valorTotal);
        return true;
    }

    /**
     * Obtiene las etiquetas de control interno generadas por los lotes de esta entrega
     * que implementan la interfaz Etiquetable (Metales y Residuos Electrónicos).
     *
     * @return Lista de cadenas con las etiquetas generadas.
     */
    public List<String> obtenerEtiquetas() {
        List<String> etiquetas = new ArrayList<>();
        for (Lote lote : lotes) {
            if (lote instanceof Etiquetable etiquetable) {
                etiquetas.add(etiquetable.generarEtiqueta());
            }
        }
        return etiquetas;
    }

    @Override
    public String toString() {
        return String.format("Entrega [código=%s, fecha=%s, aportante=%s (%s), estado=%s, lotes=%d, comprobante=%s]",
                codigo, fecha, aportante.nombre(), aportante.identificacion(), estado, lotes.size(),
                (comprobante != null ? "SÍ" : "NO"));
    }
}
