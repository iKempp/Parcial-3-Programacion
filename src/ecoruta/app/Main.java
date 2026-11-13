package ecoruta.app;

import ecoruta.modelo.*;
import ecoruta.pruebas.PruebasEcoRuta;
import ecoruta.servicio.EcoRutaServicio;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal que contiene el menú interactivo por consola.
 * Permite probar e interactuar con todas las funcionalidades del sistema EcoRuta
 * y ejecutar las 5 pruebas funcionales exigidas.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final EcoRutaServicio servicio = new EcoRutaServicio();

    public static void main(String[] args) {
        // Cargar aportantes iniciales para facilitar la interacción
        servicio.cargarAportantesIniciales();

        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1 -> registrarAportante();
                case 2 -> registrarEntrega();
                case 3 -> agregarLoteAEntrega();
                case 4 -> aceptarEntrega();
                case 5 -> consultarComprobanteYEtiquetas();
                case 6 -> consultarValorAcumuladoAportante();
                case 7 -> listarEntregas();
                case 8 -> listarAportantes();
                case 9 -> ejecutarPruebasFuncionales();
                case 0 -> {
                    System.out.println("Gracias por utilizar el sistema EcoRuta. ¡Hasta pronto!");
                    salir = true;
                }
                default -> System.out.println("Opción no válida. Por favor intente de nuevo.");
            }
            if (!salir) {
                System.out.println("\nPresione ENTER para continuar...");
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                } else {
                    salir = true;
                }
            }
        }
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("====================================================================");
        System.out.println("         EcoRuta · Recepción y Valoración de Materiales             ");
        System.out.println("====================================================================");
        System.out.println(" 1. Registrar aportante");
        System.out.println(" 2. Registrar entrega");
        System.out.println(" 3. Agregar lote a una entrega abierta");
        System.out.println(" 4. Aceptar entrega (cerrar y generar comprobante)");
        System.out.println(" 5. Consultar comprobante y etiquetas por código de entrega");
        System.out.println(" 6. Consultar valor acumulado por aportante");
        System.out.println(" 7. Listar entregas registradas");
        System.out.println(" 8. Listar aportantes registrados");
        System.out.println(" 9. Ejecutar las 5 Pruebas Funcionales requeridas");
        System.out.println(" 0. Salir");
        System.out.println("====================================================================");
    }

    private static void registrarAportante() {
        System.out.println("--- REGISTRO DE APORTANTE ---");
        System.out.print("Identificación (cédula o NIT): ");
        String id = scanner.nextLine().trim();
        System.out.print("Nombre completo o razón social: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Correo electrónico: ");
        String correo = scanner.nextLine().trim();

        try {
            Aportante aportante = new Aportante(id, nombre, correo);
            boolean ok = servicio.registrarAportante(aportante);
            if (ok) {
                System.out.println(">> Aportante registrado con éxito: " + aportante);
            } else {
                System.out.println(">> Rechazado: Ya existe un aportante con la identificación '" + id + "'.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(">> Error de validación: " + e.getMessage());
        }
    }

    private static void registrarEntrega() {
        System.out.println("--- REGISTRO DE ENTREGA ---");
        System.out.print("Código único de la entrega (ej: E01): ");
        String codigo = scanner.nextLine().trim();
        System.out.print("Identificación del aportante: ");
        String idAportante = scanner.nextLine().trim();

        boolean ok = servicio.registrarEntrega(codigo, LocalDate.now(), idAportante);
        if (ok) {
            System.out.println(">> Entrega '" + codigo + "' registrada con éxito en estado ABIERTA.");
        } else {
            System.out.println(">> Rechazado: El código ya existe o el aportante con ID '" + idAportante + "' no está registrado.");
        }
    }

    private static void agregarLoteAEntrega() {
        System.out.println("--- AGREGAR LOTE A ENTREGA ABIERTA ---");
        System.out.print("Código de la entrega a la que pertenece el lote: ");
        String codEntrega = scanner.nextLine().trim();

        Entrega entrega = servicio.buscarEntrega(codEntrega);
        if (entrega == null) {
            System.out.println(">> Rechazado: La entrega '" + codEntrega + "' no existe.");
            return;
        }
        if (entrega.getEstado() != EstadoEntrega.ABIERTA) {
            System.out.println(">> Rechazado: La entrega se encuentra en estado " + entrega.getEstado() + " y está cerrada a nuevos lotes.");
            return;
        }

        System.out.println("Seleccione el tipo de material:");
        System.out.println(" 1. Papel y cartón");
        System.out.println(" 2. Metales (Aluminio / Acero)");
        System.out.println(" 3. Residuos electrónicos");
        int tipo = leerEntero("Opción: ");

        System.out.print("Código único del lote (ej: L01): ");
        String codLote = scanner.nextLine().trim();
        System.out.print("Descripción del lote: ");
        String desc = scanner.nextLine().trim();
        int pesoKg = leerEntero("Peso en kilogramos (entero positivo): ");

        if (pesoKg <= 0) {
            System.out.println(">> Rechazado: El peso en kilogramos debe ser un entero positivo (> 0).");
            return;
        }

        Lote lote = null;
        try {
            switch (tipo) {
                case 1 -> {
                    System.out.print("¿El material fue clasificado antes de entregarse? (s/n): ");
                    String resp = scanner.nextLine().trim().toLowerCase();
                    boolean clasificado = resp.startsWith("s");
                    lote = new LotePapelCarton(codLote, desc, pesoKg, clasificado);
                }
                case 2 -> {
                    System.out.println("Tipo de metal: 1. ALUMINIO ($2.200/kg) | 2. ACERO ($900/kg)");
                    int selMetal = leerEntero("Seleccione (1 o 2): ");
                    TipoMetal tipoMetal = (selMetal == 2) ? TipoMetal.ACERO : TipoMetal.ALUMINIO;
                    lote = new LoteMetal(codLote, desc, pesoKg, tipoMetal);
                }
                case 3 -> {
                    int equipos = leerEntero("Cantidad de equipos completos que contiene (>= 0): ");
                    if (equipos < 0) {
                        System.out.println(">> Rechazado: La cantidad de equipos no puede ser negativa.");
                        return;
                    }
                    lote = new LoteElectronico(codLote, desc, pesoKg, equipos);
                }
                default -> {
                    System.out.println(">> Tipo de material inválido.");
                    return;
                }
            }

            boolean agregado = servicio.agregarLoteAEntrega(codEntrega, lote);
            if (agregado) {
                System.out.printf(">> Lote agregado exitosamente. Valor calculado: $%,.2f%n", lote.calcularValor());
            } else {
                System.out.println(">> Rechazado: El código de lote ya existe en esta entrega o la entrega no está abierta.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(">> Error al crear el lote: " + e.getMessage());
        }
    }

    private static void aceptarEntrega() {
        System.out.println("--- ACEPTAR ENTREGA ---");
        System.out.print("Código de la entrega a aceptar: ");
        String codigo = scanner.nextLine().trim();

        Entrega entrega = servicio.buscarEntrega(codigo);
        if (entrega == null) {
            System.out.println(">> Rechazado: La entrega '" + codigo + "' no existe.");
            return;
        }

        if (entrega.getEstado() == EstadoEntrega.ACEPTADA) {
            System.out.println(">> Rechazado: La entrega '" + codigo + "' ya fue aceptada previamente.");
            return;
        }

        if (entrega.getLotes().isEmpty()) {
            System.out.println(">> Rechazado: La entrega está vacía (no contiene lotes). Debe tener al menos un lote para ser aceptada.");
            return;
        }

        boolean aceptada = servicio.aceptarEntrega(codigo);
        if (aceptada) {
            Comprobante comp = entrega.getComprobante();
            System.out.println(">> ¡Entrega aceptada con éxito!");
            System.out.println("---------------------------------------------------------");
            System.out.println("COMPROBANTE GENERADO:");
            System.out.println(" - Código de entrega:        " + comp.codigoEntrega());
            System.out.println(" - Identificación aportante: " + comp.identificacionAportante());
            System.out.println(" - Peso total:               " + comp.pesoTotalKg() + " kg");
            System.out.printf(" - Valor total reconocido:   $%,.2f%n", comp.valorTotal());
            System.out.println("---------------------------------------------------------");
        } else {
            System.out.println(">> Rechazado: No se pudo aceptar la entrega.");
        }
    }

    private static void consultarComprobanteYEtiquetas() {
        System.out.println("--- CONSULTA DE COMPROBANTE Y ETIQUETAS ---");
        System.out.print("Código de la entrega: ");
        String codigo = scanner.nextLine().trim();

        Entrega entrega = servicio.buscarEntrega(codigo);
        if (entrega == null) {
            System.out.println(">> La entrega '" + codigo + "' no existe.");
            return;
        }

        System.out.println("Estado de la entrega: " + entrega.getEstado());
        if (entrega.getEstado() != EstadoEntrega.ACEPTADA) {
            System.out.println(">> La entrega está ABIERTA y no cuenta aún con comprobante de aceptación.");
        } else {
            Comprobante comp = entrega.getComprobante();
            System.out.println("---------------------------------------------------------");
            System.out.println("COMPROBANTE:");
            System.out.println(" - Código de Entrega:        " + comp.codigoEntrega());
            System.out.println(" - Identificación Aportante: " + comp.identificacionAportante());
            System.out.println(" - Peso Total:               " + comp.pesoTotalKg() + " kg");
            System.out.printf(" - Valor Total Reconocido:   $%,.2f%n", comp.valorTotal());
            System.out.println("---------------------------------------------------------");
        }

        List<String> etiquetas = servicio.consultarEtiquetas(codigo);
        System.out.println("ETIQUETAS DE CONTROL INTERNO (" + etiquetas.size() + "):");
        if (etiquetas.isEmpty()) {
            System.out.println(" - (Esta entrega no tiene lotes de metal ni de residuos electrónicos)");
        } else {
            for (String etiqueta : etiquetas) {
                System.out.println(" - " + etiqueta);
            }
        }
    }

    private static void consultarValorAcumuladoAportante() {
        System.out.println("--- CONSULTA DE VALOR ACUMULADO POR APORTANTE ---");
        System.out.print("Identificación del aportante: ");
        String id = scanner.nextLine().trim();

        Aportante aportante = servicio.buscarAportante(id);
        if (aportante != null) {
            System.out.println("Aportante: " + aportante.nombre() + " (" + aportante.correo() + ")");
        } else {
            System.out.println("(Nota: Identificación no encontrada en registros)");
        }

        double acumulado = servicio.consultarValorAcumuladoAportante(id);
        System.out.printf(">> Valor acumulado reconocido (únicamente entregas aceptadas): $%,.2f%n", acumulado);
    }

    private static void listarEntregas() {
        System.out.println("--- LISTADO DE ENTREGAS ---");
        Collection<Entrega> entregas = servicio.listarEntregas();
        if (entregas.isEmpty()) {
            System.out.println("No hay entregas registradas.");
            return;
        }
        for (Entrega e : entregas) {
            System.out.printf("Entrega [%s] | Fecha: %s | Estado: %s | Aportante: %s (%s) | Lotes: %d | Comprobante: %s%n",
                    e.getCodigo(), e.getFecha(), e.getEstado(),
                    e.getAportante().nombre(), e.getAportante().identificacion(),
                    e.getLotes().size(),
                    (e.getComprobante() != null ? String.format("$%,.2f", e.getComprobante().valorTotal()) : "No"));
            for (Lote l : e.getLotes()) {
                System.out.printf("    -> %s%n", l);
            }
        }
    }

    private static void listarAportantes() {
        System.out.println("--- APORTANTES REGISTRADOS ---");
        Collection<Aportante> aportantes = servicio.listarAportantes();
        if (aportantes.isEmpty()) {
            System.out.println("No hay aportantes registrados.");
            return;
        }
        for (Aportante a : aportantes) {
            System.out.printf(" - ID: %-10s | Nombre: %-25s | Correo: %s%n",
                    a.identificacion(), a.nombre(), a.correo());
        }
    }

    private static void ejecutarPruebasFuncionales() {
        PruebasEcoRuta.ejecutarTodas();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!scanner.hasNextLine()) {
                return 0; // Salida segura en caso de fin de flujo
            }
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(">> Ingrese un número entero válido.");
            }
        }
    }
}
