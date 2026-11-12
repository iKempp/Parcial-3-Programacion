package ecoruta.pruebas;

import ecoruta.modelo.*;
import ecoruta.servicio.EcoRutaServicio;

import java.time.LocalDate;
import java.util.List;

/**
 * Suite de pruebas funcionales solicitadas en el enunciado del parcial EcoRuta.
 * Registra para cada prueba:
 * - Datos de entrada
 * - Resultado esperado
 * - Resultado obtenido
 * - Evaluación de éxito (APROBADA / FALLIDA)
 */
public class PruebasEcoRuta {

    public static void main(String[] args) {
        ejecutarTodas();
    }

    /**
     * Ejecuta las cinco pruebas funcionales obligatorias y muestra el reporte detallado.
     */
    public static boolean ejecutarTodas() {
        System.out.println("================================================================================");
        System.out.println("                    PRUEBAS FUNCIONALES DE ECORUTA                              ");
        System.out.println("================================================================================");

        boolean todasExitosas = true;

        todasExitosas &= prueba1RegistroEntrega();
        todasExitosas &= prueba2ValidacionLotes();
        todasExitosas &= prueba3RechazoEntregaVacia();
        todasExitosas &= prueba4AceptacionYCalculoTresLotes();
        todasExitosas &= prueba5ConsultaEtiquetas();

        System.out.println("================================================================================");
        if (todasExitosas) {
            System.out.println(">>> RESULTADO GLOBAL: TODAS LAS PRUEBAS FUNCIONALES PASARON EXITOSAMENTE (5/5) <<<");
        } else {
            System.out.println(">>> RESULTADO GLOBAL: HUBO PRUEBAS CON FALLAS <<<");
        }
        System.out.println("================================================================================\n");

        return todasExitosas;
    }

    /**
     * Prueba 1. Registro de una entrega.
     * Con un aportante registrado, cree la entrega E01. Debe quedar en estado ABIERTA, sin lotes
     * y sin comprobante. Intente registrar otra entrega con el código E01 y luego una con un
     * código nuevo y un aportante desconocido. Ambas operaciones deben rechazarse y E01 debe conservarse.
     */
    public static boolean prueba1RegistroEntrega() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PRUEBA 1: Registro de una entrega y validación de duplicados y aportante");
        System.out.println("--------------------------------------------------------------------------------");

        EcoRutaServicio servicio = new EcoRutaServicio();
        Aportante aportante1 = new Aportante("1001", "María Rodríguez", "maria@mail.com");
        servicio.registrarAportante(aportante1);

        System.out.println("[DATOS DE ENTRADA]");
        System.out.println(" - Aportante registrado: ID '1001' (María Rodríguez)");
        System.out.println(" - Registro inicial: Código 'E01', Fecha '2026-11-12', Aportante '1001'");
        System.out.println(" - Intento 1 inválido: Código repetido 'E01', Aportante '1001'");
        System.out.println(" - Intento 2 inválido: Código nuevo 'E02', Aportante desconocido '9999'");

        // Ejecución
        boolean regE01 = servicio.registrarEntrega("E01", LocalDate.now(), "1001");
        Entrega e01 = servicio.buscarEntrega("E01");

        boolean regDuplicado = servicio.registrarEntrega("E01", LocalDate.now(), "1001");
        boolean regAportanteDesconocido = servicio.registrarEntrega("E02", LocalDate.now(), "9999");
        Entrega e01Despues = servicio.buscarEntrega("E01");
        Entrega e02 = servicio.buscarEntrega("E02");

        System.out.println("\n[RESULTADO ESPERADO]");
        System.out.println(" - E01 creada con éxito en estado ABIERTA, con 0 lotes y sin comprobante (null).");
        System.out.println(" - Intento con código E01 repetido: rechazado (false).");
        System.out.println(" - Intento con aportante '9999' desconocido: rechazado (false). E02 no debe crearse.");
        System.out.println(" - E01 debe conservarse intacta en el sistema.");

        boolean estadoAbierta = (e01 != null && e01.getEstado() == EstadoEntrega.ABIERTA);
        boolean sinLotes = (e01 != null && e01.getLotes().isEmpty());
        boolean sinComprobante = (e01 != null && e01.getComprobante() == null);
        boolean duplicadoRechazado = !regDuplicado;
        boolean desconocidoRechazado = (!regAportanteDesconocido && e02 == null);
        boolean e01Conservada = (e01Despues != null && e01Despues.getCodigo().equals("E01"));

        boolean exito = regE01 && estadoAbierta && sinLotes && sinComprobante
                && duplicadoRechazado && desconocidoRechazado && e01Conservada;

        System.out.println("\n[RESULTADO OBTENIDO]");
        System.out.printf(" - Registro E01: %s | Estado: %s | Lotes: %d | Comprobante: %s%n",
                (regE01 ? "CREADA" : "FALLÓ"),
                (e01 != null ? e01.getEstado() : "null"),
                (e01 != null ? e01.getLotes().size() : -1),
                (e01 != null && e01.getComprobante() != null ? "Generado" : "null"));
        System.out.printf(" - Intento duplicado E01: %s%n", (regDuplicado ? "Aceptado (ERROR)" : "RECHAZADO"));
        System.out.printf(" - Intento aportante '9999': %s | E02 creada: %s%n",
                (regAportanteDesconocido ? "Aceptado (ERROR)" : "RECHAZADO"),
                (e02 != null ? "SÍ" : "NO"));
        System.out.printf(" - Conservación de E01: %s%n", (e01Conservada ? "CONSERVADA" : "NO ENCONTRADA"));

        System.out.println(">> EVALUACIÓN: " + (exito ? "APROBADA [OK]" : "FALLIDA [ERROR]") + "\n");
        return exito;
    }

    /**
     * Prueba 2. Validación de lotes.
     * En una entrega abierta, agregue el lote L01 con 5 kg. Luego intente agregar otro lote
     * con código L01 y un lote distinto con 0 kg. Ambas operaciones deben rechazarse y la
     * entrega debe conservar únicamente el primer lote.
     */
    public static boolean prueba2ValidacionLotes() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PRUEBA 2: Validación de lotes (código único y peso positivo)");
        System.out.println("--------------------------------------------------------------------------------");

        EcoRutaServicio servicio = new EcoRutaServicio();
        servicio.registrarAportante(new Aportante("1001", "María Rodríguez", "maria@mail.com"));
        servicio.registrarEntrega("E01", LocalDate.now(), "1001");
        Entrega entrega = servicio.buscarEntrega("E01");

        System.out.println("[DATOS DE ENTRADA]");
        System.out.println(" - Entrega abierta: 'E01'");
        System.out.println(" - Lote inicial válido: Código 'L01', Descripción 'Papel bond', Peso 5 kg, Clasificado: false");
        System.out.println(" - Intento 1 inválido: Lote con código repetido 'L01', Peso 8 kg");
        System.out.println(" - Intento 2 inválido: Lote con código distinto 'L02', Peso 0 kg");

        // Ejecución
        Lote lote1 = new LotePapelCarton("L01", "Papel bond", 5, false);
        boolean agLote1 = entrega.agregarLote(lote1);

        // Intento código repetido
        Lote loteRepetido = new LotePapelCarton("L01", "Cartón duplicado", 8, true);
        boolean agRepetido = entrega.agregarLote(loteRepetido);

        // Intento peso 0 kg (puede fallar al instanciar si valida el constructor o al agregar)
        boolean agPesoCero = false;
        try {
            Lote loteCero = new LotePapelCarton("L02", "Cartón sin peso", 0, false);
            agPesoCero = entrega.agregarLote(loteCero);
        } catch (IllegalArgumentException e) {
            // Rechazado correctamente por validación de dominio de peso > 0
            agPesoCero = false;
        }

        System.out.println("\n[RESULTADO ESPERADO]");
        System.out.println(" - Lote L01 (5 kg) agregado exitosamente (true).");
        System.out.println(" - Intento con código duplicado L01: rechazado (false).");
        System.out.println(" - Intento con peso 0 kg: rechazado (false / IllegalArgumentException).");
        System.out.println(" - La entrega debe conservar únicamente el primer lote L01 (total lotes = 1, peso = 5 kg).");

        boolean exitoLote1 = agLote1;
        boolean exitoRepetidoRechazado = !agRepetido;
        boolean exitoPesoCeroRechazado = !agPesoCero;
        boolean soloUnLote = (entrega.getLotes().size() == 1
                && entrega.getLotes().get(0).getCodigo().equals("L01")
                && entrega.getLotes().get(0).getPesoKg() == 5);

        boolean exito = exitoLote1 && exitoRepetidoRechazado && exitoPesoCeroRechazado && soloUnLote;

        System.out.println("\n[RESULTADO OBTENIDO]");
        System.out.printf(" - Adición L01 (5 kg): %s%n", (agLote1 ? "AGREGADO" : "RECHAZADO"));
        System.out.printf(" - Intento L01 duplicado: %s%n", (agRepetido ? "Aceptado (ERROR)" : "RECHAZADO"));
        System.out.printf(" - Intento Lote con 0 kg: %s%n", (agPesoCero ? "Aceptado (ERROR)" : "RECHAZADO"));
        System.out.printf(" - Cantidad total de lotes en entrega: %d (Lote presente: %s, %d kg)%n",
                entrega.getLotes().size(),
                (entrega.getLotes().isEmpty() ? "ninguno" : entrega.getLotes().get(0).getCodigo()),
                (entrega.getLotes().isEmpty() ? 0 : entrega.getLotes().get(0).getPesoKg()));

        System.out.println(">> EVALUACIÓN: " + (exito ? "APROBADA [OK]" : "FALLIDA [ERROR]") + "\n");
        return exito;
    }

    /**
     * Prueba 3. Rechazo de una entrega vacía.
     * Intente aceptar una entrega que aún no tenga lotes. El sistema debe rechazar la operación,
     * mantener la entrega en estado ABIERTA y dejarla sin comprobante.
     */
    public static boolean prueba3RechazoEntregaVacia() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PRUEBA 3: Rechazo de una entrega vacía");
        System.out.println("--------------------------------------------------------------------------------");

        EcoRutaServicio servicio = new EcoRutaServicio();
        servicio.registrarAportante(new Aportante("1001", "María Rodríguez", "maria@mail.com"));
        servicio.registrarEntrega("E03", LocalDate.now(), "1001");
        Entrega entrega = servicio.buscarEntrega("E03");

        System.out.println("[DATOS DE ENTRADA]");
        System.out.println(" - Entrega creada: 'E03' (sin lotes agregados)");
        System.out.println(" - Operación: aceptarEntrega('E03')");

        // Ejecución
        boolean resultadoAceptacion = servicio.aceptarEntrega("E03");

        System.out.println("\n[RESULTADO ESPERADO]");
        System.out.println(" - Operación de aceptación rechazada (false).");
        System.out.println(" - Estado de la entrega permanece en ABIERTA.");
        System.out.println(" - Comprobante de la entrega permanece en null (sin comprobante).");

        boolean rechazoCorrecto = !resultadoAceptacion;
        boolean permaneceAbierta = (entrega.getEstado() == EstadoEntrega.ABIERTA);
        boolean sinComprobante = (entrega.getComprobante() == null);

        boolean exito = rechazoCorrecto && permaneceAbierta && sinComprobante;

        System.out.println("\n[RESULTADO OBTENIDO]");
        System.out.printf(" - Resultado operación aceptar: %s%n", (resultadoAceptacion ? "Aceptada (ERROR)" : "RECHAZADA"));
        System.out.printf(" - Estado actual de la entrega: %s%n", entrega.getEstado());
        System.out.printf(" - Comprobante actual: %s%n", (entrega.getComprobante() == null ? "null (Sin comprobante)" : "Generado"));

        System.out.println(">> EVALUACIÓN: " + (exito ? "APROBADA [OK]" : "FALLIDA [ERROR]") + "\n");
        return exito;
    }

    /**
     * Prueba 4. Aceptación y cálculo con los tres tipos de lote.
     * Agregue a una entrega:
     * - 10 kg de papel y cartón clasificado ($6.000)
     * - 5 kg de aluminio ($11.000)
     * - 4 kg de residuos electrónicos con 2 equipos completos ($7.200)
     * Al aceptar la entrega, debe pasar a ACEPTADA y su comprobante debe mostrar 19 kg y $24.200.
     * Los valores esperados por lote son $6.000, $11.000 y $7.200, respectivamente.
     */
    public static boolean prueba4AceptacionYCalculoTresLotes() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PRUEBA 4: Aceptación y cálculo con los tres tipos de lote");
        System.out.println("--------------------------------------------------------------------------------");

        EcoRutaServicio servicio = new EcoRutaServicio();
        servicio.registrarAportante(new Aportante("1001", "María Rodríguez", "maria@mail.com"));
        servicio.registrarEntrega("E04", LocalDate.now(), "1001");

        // Creación de los tres tipos de lote
        Lote lotePapel = new LotePapelCarton("LP01", "Papel y cartón archivo", 10, true);
        Lote loteMetal = new LoteMetal("LM01", "Latas y perfiles", 5, TipoMetal.ALUMINIO);
        Lote loteElect = new LoteElectronico("LE01", "Computadores y tarjetas", 4, 2);

        System.out.println("[DATOS DE ENTRADA]");
        System.out.println(" - Entrega: 'E04' (Aportante ID '1001')");
        System.out.println(" - Lote 1 (Papel y cartón): 10 kg, clasificado = true");
        System.out.println(" - Lote 2 (Metal): 5 kg, metal = ALUMINIO");
        System.out.println(" - Lote 3 (Electrónico): 4 kg, equipos completos = 2");

        servicio.agregarLoteAEntrega("E04", lotePapel);
        servicio.agregarLoteAEntrega("E04", loteMetal);
        servicio.agregarLoteAEntrega("E04", loteElect);

        // Cálculos individuales por lote
        double valPapel = lotePapel.calcularValor();
        double valMetal = loteMetal.calcularValor();
        double valElect = loteElect.calcularValor();

        // Aceptación de la entrega
        boolean aceptada = servicio.aceptarEntrega("E04");
        Entrega entrega = servicio.buscarEntrega("E04");
        Comprobante comp = entrega != null ? entrega.getComprobante() : null;

        System.out.println("\n[RESULTADO ESPERADO]");
        System.out.println(" - Valor Lote Papel clasificado (10 kg * $600/kg): $6.000");
        System.out.println(" - Valor Lote Metal aluminio (5 kg * $2.200/kg): $11.000");
        System.out.println(" - Valor Lote Electrónico (4 kg * $1.400 + 2 * $800): $7.200");
        System.out.println(" - Estado de la entrega: ACEPTADA");
        System.out.println(" - Comprobante generado con: Peso Total = 19 kg, Valor Total = $24.200");

        boolean valPapelOk = (Double.compare(valPapel, 6000.0) == 0);
        boolean valMetalOk = (Double.compare(valMetal, 11000.0) == 0);
        boolean valElectOk = (Double.compare(valElect, 7200.0) == 0);
        boolean estadoOk = (entrega != null && entrega.getEstado() == EstadoEntrega.ACEPTADA);
        boolean compExiste = (comp != null);
        boolean pesoCompOk = (comp != null && comp.pesoTotalKg() == 19);
        boolean valorCompOk = (comp != null && Double.compare(comp.valorTotal(), 24200.0) == 0);

        boolean exito = aceptada && valPapelOk && valMetalOk && valElectOk && estadoOk && compExiste && pesoCompOk && valorCompOk;

        System.out.println("\n[RESULTADO OBTENIDO]");
        System.out.printf(" - Valor Lote Papel: $%,.2f (%s)%n", valPapel, (valPapelOk ? "OK" : "ERROR"));
        System.out.printf(" - Valor Lote Metal: $%,.2f (%s)%n", valMetal, (valMetalOk ? "OK" : "ERROR"));
        System.out.printf(" - Valor Lote Electrónico: $%,.2f (%s)%n", valElect, (valElectOk ? "OK" : "ERROR"));
        System.out.printf(" - Estado entrega: %s | Aceptación retornó: %s%n",
                (entrega != null ? entrega.getEstado() : "null"), aceptada);
        if (comp != null) {
            System.out.printf(" - Comprobante: Código='%s', Aportante='%s', Peso Total=%d kg (%s), Valor Total=$%,.2f (%s)%n",
                    comp.codigoEntrega(), comp.identificacionAportante(),
                    comp.pesoTotalKg(), (pesoCompOk ? "OK" : "ERROR"),
                    comp.valorTotal(), (valorCompOk ? "OK" : "ERROR"));
        } else {
            System.out.println(" - Comprobante: NO FUE GENERADO (ERROR)");
        }

        System.out.println(">> EVALUACIÓN: " + (exito ? "APROBADA [OK]" : "FALLIDA [ERROR]") + "\n");
        return exito;
    }

    /**
     * Prueba 5. Consulta de etiquetas.
     * Consulte las etiquetas de la entrega aceptada en la prueba anterior. Deben aparecer
     * exactamente dos. La del lote de metal debe incluir su código y el tipo ALUMINIO.
     * La del lote electrónico debe incluir su código y la cantidad de equipos completos, 2.
     */
    public static boolean prueba5ConsultaEtiquetas() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PRUEBA 5: Consulta de etiquetas de control interno");
        System.out.println("--------------------------------------------------------------------------------");

        // Reutilizamos el escenario de la prueba 4
        EcoRutaServicio servicio = new EcoRutaServicio();
        servicio.registrarAportante(new Aportante("1001", "María Rodríguez", "maria@mail.com"));
        servicio.registrarEntrega("E04", LocalDate.now(), "1001");

        Lote lotePapel = new LotePapelCarton("LP01", "Papel y cartón archivo", 10, true);
        Lote loteMetal = new LoteMetal("LM01", "Latas y perfiles", 5, TipoMetal.ALUMINIO);
        Lote loteElect = new LoteElectronico("LE01", "Computadores y tarjetas", 4, 2);

        servicio.agregarLoteAEntrega("E04", lotePapel);
        servicio.agregarLoteAEntrega("E04", loteMetal);
        servicio.agregarLoteAEntrega("E04", loteElect);
        servicio.aceptarEntrega("E04");

        System.out.println("[DATOS DE ENTRADA]");
        System.out.println(" - Consulta de etiquetas de la entrega 'E04' que contiene 3 lotes:");
        System.out.println("   * LP01: Papel y cartón (sin etiqueta)");
        System.out.println("   * LM01: Metal (ALUMINIO)");
        System.out.println("   * LE01: Residuo electrónico (2 equipos completos)");

        // Ejecución
        List<String> etiquetas = servicio.consultarEtiquetas("E04");

        System.out.println("\n[RESULTADO ESPERADO]");
        System.out.println(" - Cantidad de etiquetas: exactamente 2.");
        System.out.println(" - Etiqueta de metal: debe incluir el código 'LM01' y el tipo 'ALUMINIO'.");
        System.out.println(" - Etiqueta de residuo electrónico: debe incluir el código 'LE01' y equipos completos '2'.");
        System.out.println(" - El lote de papel y cartón NO debe generar etiqueta.");

        boolean cantidadOk = (etiquetas.size() == 2);
        boolean metalEtiquetaOk = false;
        boolean electEtiquetaOk = false;

        for (String etq : etiquetas) {
            if (etq.contains("LM01") && etq.contains("ALUMINIO")) {
                metalEtiquetaOk = true;
            }
            if (etq.contains("LE01") && etq.contains("2")) {
                electEtiquetaOk = true;
            }
        }

        boolean exito = cantidadOk && metalEtiquetaOk && electEtiquetaOk;

        System.out.println("\n[RESULTADO OBTENIDO]");
        System.out.printf(" - Total etiquetas generadas: %d (%s)%n", etiquetas.size(), (cantidadOk ? "OK" : "ERROR"));
        for (int i = 0; i < etiquetas.size(); i++) {
            System.out.printf("   [%d] %s%n", (i + 1), etiquetas.get(i));
        }
        System.out.printf(" - Etiqueta de Metal contiene código y tipo ALUMINIO: %s%n", (metalEtiquetaOk ? "SÍ (OK)" : "NO (ERROR)"));
        System.out.printf(" - Etiqueta Electrónico contiene código y equipos = 2: %s%n", (electEtiquetaOk ? "SÍ (OK)" : "NO (ERROR)"));

        System.out.println(">> EVALUACIÓN: " + (exito ? "APROBADA [OK]" : "FALLIDA [ERROR]") + "\n");
        return exito;
    }
}
