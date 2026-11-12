package ecoruta.modelo;

/**
 * Record que representa el comprobante generado al aceptar una entrega.
 * Sus datos son inmutables: código de entrega, identificación del aportante,
 * peso total y valor total reconocido.
 *
 * @param codigoEntrega Identificador único de la entrega aceptada.
 * @param identificacionAportante Documento o identificación del aportante.
 * @param pesoTotalKg Peso total en kilogramos de todos los lotes de la entrega.
 * @param valorTotal Valor total monetario reconocido al aportante.
 */
public record Comprobante(String codigoEntrega, String identificacionAportante, int pesoTotalKg, double valorTotal) {

    public Comprobante {
        if (codigoEntrega == null || codigoEntrega.isBlank()) {
            throw new IllegalArgumentException("El código de entrega no puede estar vacío.");
        }
        if (identificacionAportante == null || identificacionAportante.isBlank()) {
            throw new IllegalArgumentException("La identificación del aportante no puede estar vacía.");
        }
        if (pesoTotalKg <= 0) {
            throw new IllegalArgumentException("El peso total debe ser un número entero positivo.");
        }
        if (valorTotal < 0) {
            throw new IllegalArgumentException("El valor total no puede ser negativo.");
        }
        codigoEntrega = codigoEntrega.trim();
        identificacionAportante = identificacionAportante.trim();
    }
}
