package ecoruta.modelo;

/**
 * Record que representa a un aportante en EcoRuta.
 * Sus datos de registro (identificación, nombre y correo) son inmutables
 * y no se modifican después de ser creados.
 *
 * @param identificacion Documento o código de identificación único.
 * @param nombre Nombre completo o razón social del aportante.
 * @param correo Correo electrónico de contacto.
 */
public record Aportante(String identificacion, String nombre, String correo) {

    public Aportante {
        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException("La identificación no puede estar vacía.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("El correo no puede estar vacío.");
        }
        identificacion = identificacion.trim();
        nombre = nombre.trim();
        correo = correo.trim();
    }
}
