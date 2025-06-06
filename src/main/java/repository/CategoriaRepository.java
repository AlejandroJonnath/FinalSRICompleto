package repository; // Define el paquete donde se ubica este repositorio

import model.Categoria; // Importa el modelo Categoria
import util.Conexion; // Importa la utilidad para obtener conexiones JDBC

import java.sql.Connection; // Importa la clase Connection de JDBC
import java.sql.PreparedStatement; // Importa la clase PreparedStatement para consultas parametrizadas
import java.sql.ResultSet; // Importa la clase ResultSet para leer resultados de consultas
import java.sql.SQLException; // Importa la excepción lanzada por errores JDBC
import java.util.ArrayList; // Importa ArrayList para colecciones dinámicas
import java.util.List; // Importa la interfaz List

public class CategoriaRepository { // Clase que maneja operaciones CRUD para categorías

    public List<Categoria> listarTodas() { // Método que devuelve todas las categorías
        List<Categoria> lista = new ArrayList<>(); // Crea la lista donde se almacenarán las categorías
        String sql = "SELECT id, nombre FROM categorias"; // Consulta SQL para obtener id y nombre

        try (Connection conn = Conexion.getConnection(); // Obtiene conexión a la base de datos
             PreparedStatement ps = conn.prepareStatement(sql); // Prepara la consulta SQL
             ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y obtiene el resultado

            while (rs.next()) { // Itera sobre cada fila del resultado
                Categoria c = new Categoria(); // Crea una nueva instancia de Categoria
                c.setId(rs.getInt("id")); // Asigna el id leído de la columna "id"
                c.setNombre(rs.getString("nombre")); // Asigna el nombre leído de la columna "nombre"
                lista.add(c); // Agrega la categoría a la lista
            }

        } catch (SQLException e) { // Maneja excepciones SQL
            e.printStackTrace(); // Imprime el stack trace en caso de error
        }

        return lista; // Devuelve la lista de categorías
    }
}
