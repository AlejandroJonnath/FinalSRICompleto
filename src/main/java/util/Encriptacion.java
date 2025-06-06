package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase utilitaria para encriptación mediante SHA-1.
 */
public class Encriptacion {

    /**
     * Encripta una cadena de texto usando el algoritmo SHA-1.
     *
     * @param input Texto plano a encriptar.
     * @return Cadena hexadecimal que representa el hash SHA-1 del input.
     */
    public static String sha1(String input) {
        try {
            // Obtiene instancia del algoritmo SHA-1
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");

            // Calcula el digest (hash) del texto de entrada en bytes
            byte[] result = mDigest.digest(input.getBytes());

            // Convierte los bytes en una cadena hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // Lanza una excepción en tiempo de ejecución si SHA-1 no está disponible
            throw new RuntimeException("Error al encriptar con SHA1", e);
        }
    }
}
