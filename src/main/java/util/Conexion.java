package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para manejar la conexión a la base de datos MySQL.
 */
public class Conexion {

    // URL de conexión a la base de datos, con el puerto y nombre de la base
    private static final String URL = "jdbc:mysql://localhost:3306/mimir_petshop";

    // Usuario de la base de datos
    private static final String USER = "root";

    // Contraseña del usuario de la base de datos (no hay contraseña ekisde)
    private static final String PASS = "";

    /**
     * Método estático para obtener una conexión a la base de datos.
     *
     * @return Connection Objeto conexión
     * @throws SQLException Si ocurre un error al conectar
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
