package ecoruta.servicio;

import ecoruta.modelo.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Servicio de negocio que gestiona las operaciones en memoria de EcoRuta:
 * - Aportantes registrados.
 * - Registro y aceptación de entregas.
 * - Validación y adición de lotes.
 * - Consulta de comprobantes y etiquetas por entrega.
 * - Cálculo de valor acumulado por aportante.
 */
public class EcoRutaServicio {

    private final Map<String, Aportante> aportantes;
    private final Map<String, Entrega> entregas;

    public EcoRutaServicio() {
        this.aportantes = new LinkedHashMap<>();
        this.entregas = new LinkedHashMap<>();
    }

    /**
     * Carga aportantes iniciales de ejemplo para demostración.
     */
    public void cargarAportantesIniciales() {
        registrarAportante(new Aportante("1001", "María Rodríguez", "maria.rodriguez@email.com"));
        registrarAportante(new Aportante("1002", "Juan David Gómez", "juan.gomez@email.com"));
        registrarAportante(new Aportante("1003", "EcoSoluciones S.A.S.", "contacto@ecosoluciones.com"));
    }

    /**
     * Registra un nuevo aportante en memoria si su identificación no existe.
     *
     * @param aportante Aportante a registrar.
     * @return true si se registró; false si ya existe o es nulo.
     */
    public boolean registrarAportante(Aportante aportante) {
        if (aportante == null) {
            return false;
        }
        if (aportantes.containsKey(aportante.identificacion())) {
            return false;
        }
        aportantes.put(aportante.identificacion(), aportante);
        return true;
    }

    /**
     * Busca un aportante registrado por su identificación.
     *
     * @param identificacion Identificación del aportante.
     * @return Aportante si existe, o null en caso contrario.
     */
    public Aportante buscarAportante(String identificacion) {
        if (identificacion == null) {
            return null;
        }
        return aportantes.get(identificacion.trim());
    }

    /**
     * Obtiene la colección de todos los aportantes registrados.
     */
    public Collection<Aportante> listarAportantes() {
        return Collections.unmodifiableCollection(aportantes.values());
    }

    /**
     * Registra una nueva entrega para un aportante existente.
     * Rechaza la operación si el código ya existe o si la identificación es desconocida.
     *
     * @param codigo Código único de la entrega.
     * @param fecha Fecha de la entrega.
     * @param identificacionAportante Identificación del aportante registrado.
     * @return true si la entrega fue creada; false si fue rechazada.
     */
    public boolean registrarEntrega(String codigo, LocalDate fecha, String identificacionAportante) {
        if (codigo == null || codigo.isBlank()) {
            return false;
        }
        String codigoLimpio = codigo.trim();
        // Rechazar código repetido
        if (entregas.containsKey(codigoLimpio)) {
            return false;
        }
        // Rechazar identificación desconocida
        Aportante aportante = buscarAportante(identificacionAportante);
        if (aportante == null) {
            return false;
        }
        LocalDate fechaValida = (fecha != null) ? fecha : LocalDate.now();
        Entrega nuevaEntrega = new Entrega(codigoLimpio, fechaValida, aportante);
        entregas.put(codigoLimpio, nuevaEntrega);
        return true;
    }

    /**
     * Busca una entrega por su código.
     *
     * @param codigo Código de la entrega.
     * @return Entrega encontrada o null si no existe.
     */
    public Entrega buscarEntrega(String codigo) {
        if (codigo == null) {
            return null;
        }
        return entregas.get(codigo.trim());
    }

    /**
     * Obtiene la colección de todas las entregas registradas.
     */
    public Collection<Entrega> listarEntregas() {
        return Collections.unmodifiableCollection(entregas.values());
    }

    /**
     * Agrega un lote a una entrega existente.
     *
     * @param codigoEntrega Código de la entrega receptora.
     * @param lote Lote a agregar.
     * @return true si se agregó con éxito; false si fue rechazado.
     */
    public boolean agregarLoteAEntrega(String codigoEntrega, Lote lote) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return false;
        }
        return entrega.agregarLote(lote);
    }

    /**
     * Acepta una entrega por su código.
     * Si la entrega no existe, está vacía o ya fue aceptada, se rechaza la aceptación
     * y se conserva la información registrada.
     *
     * @param codigoEntrega Código de la entrega a aceptar.
     * @return true si la entrega fue aceptada; false si fue rechazada.
     */
    public boolean aceptarEntrega(String codigoEntrega) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return false;
        }
        return entrega.aceptar();
    }

    /**
     * Consulta el comprobante de una entrega por su código.
     * Solo retorna el comprobante si la entrega existe y fue aceptada.
     *
     * @param codigoEntrega Código de la entrega.
     * @return Comprobante inmutable o null si no existe o no ha sido aceptada.
     */
    public Comprobante consultarComprobante(String codigoEntrega) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return null;
        }
        return entrega.getComprobante();
    }

    /**
     * Consulta las etiquetas de control interno generadas por los lotes de una entrega.
     *
     * @param codigoEntrega Código de la entrega.
     * @return Lista de etiquetas (vacía si no existe o no tiene lotes con etiqueta).
     */
    public List<String> consultarEtiquetas(String codigoEntrega) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return Collections.emptyList();
        }
        return entrega.obtenerEtiquetas();
    }

    /**
     * Consulta el valor acumulado por un aportante sumando únicamente
     * los comprobantes de sus entregas aceptadas.
     * Si no hay comprobantes para esa identificación, devuelve $0.
     *
     * @param identificacion Identificación del aportante.
     * @return Valor acumulado total en pesos ($).
     */
    public double consultarValorAcumuladoAportante(String identificacion) {
        if (identificacion == null || identificacion.isBlank()) {
            return 0.0;
        }
        String idLimpia = identificacion.trim();
        double totalAcumulado = 0.0;

        for (Entrega entrega : entregas.values()) {
            if (entrega.getAportante().identificacion().equalsIgnoreCase(idLimpia)
                    && entrega.getEstado() == EstadoEntrega.ACEPTADA
                    && entrega.getComprobante() != null) {
                totalAcumulado += entrega.getComprobante().valorTotal();
            }
        }
        return totalAcumulado;
    }
}
