package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase utilitaria para encriptación mediante SHA-1.
 *
 * Sección: Clase Encriptacion
 * Esta clase contiene métodos estáticos relacionados con el hashing de texto,
 * específicamente para generar un hash SHA-1 a partir de una cadena dada.
 */
public class Encriptacion {

    /**
     * Encripta una cadena de texto usando el algoritmo SHA-1.
     *
     * Sección: Método sha1
     * Este método recibe un texto plano y devuelve su representación en hash SHA-1
     * codificada como una cadena hexadecimal.
     *
     * Si se llega a modificar este método, podría afectar la forma en que se generan
     * los hashes, rompiendo potencialmente la seguridad o la interoperabilidad con
     * otros sistemas que dependan de SHA-1.
     */
    public static String sha1(String input) {
        try {
            // Obtiene instancia del algoritmo SHA-1 para calcular el hash
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");

            // Convierte el texto plano en un arreglo de bytes y calcula su hash
            byte[] result = mDigest.digest(input.getBytes());

            // Construye la representación hexadecimal del hash
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                // Para cada byte, lo convierte a dos dígitos hexadecimales y los agrega al StringBuilder
                sb.append(String.format("%02x", b));
            }

            // Devuelve la cadena hexadecimal resultante que representa el hash SHA-1
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // Si el algoritmo SHA-1 no está disponible, lanza una excepción de ejecución con un mensaje
            throw new RuntimeException("Error al encriptar con SHA1", e);
        }
    }
}
