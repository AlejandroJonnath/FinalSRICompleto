package repository; // Define el paquete donde se ubica este repositorio

// Importaciones de clases necesarias
import model.Usuario; // Importa el modelo Usuario para manejar datos de usuario
import util.Conexion; // Importa la clase que provee conexión JDBC a la base de datos
import util.Encriptacion; // Importa la clase para encriptar contraseñas

import java.sql.*; // Importa clases JDBC: Connection, PreparedStatement, ResultSet, SQLException
import java.util.ArrayList; // Importa ArrayList para listas dinámicas
import java.util.List; // Importa la interfaz List para colecciones

/**
 * Clase UserRepository
 * Implementa la interfaz IUserRepository para operaciones CRUD sobre la tabla usuarios
 */
public class UserRepository implements IUserRepository {

    /*-------------------------- Sección: Validación de usuario --------------------------*/

    /**
     * Valida las credenciales de usuario.
     * Si coinciden usuario y contraseña, retorna el objeto Usuario con sus datos.
     * Si no, retorna null.
     *
     * Si se modifica esta función, podría afectar la seguridad del sistema
     * y el correcto reconocimiento de usuarios en el sistema.
     */
    @Override
    public Usuario validarUsuario(String usuario, String contrasena) {
        // Consulta SQL parametrizada para buscar usuario con contraseña encriptada
        String sql = "SELECT id, usuario, contrasena FROM usuarios WHERE usuario = ? AND contrasena = ?";
        try (
                Connection conn = Conexion.getConnection(); // Abre conexión a la base de datos
                PreparedStatement ps = conn.prepareStatement(sql) // Prepara la sentencia SQL
        ) {
            ps.setString(1, usuario); // Asigna el valor del usuario al primer parámetro
            ps.setString(2, Encriptacion.sha1(contrasena)); // Encripta y asigna contraseña al segundo parámetro

            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y obtiene resultados
                if (rs.next()) { // Si encuentra un registro que coincida
                    Usuario u = new Usuario(); // Crea instancia de Usuario
                    u.setId(rs.getInt("id")); // Asigna ID desde la base de datos
                    u.setUsuario(rs.getString("usuario")); // Asigna nombre de usuario
                    u.setContrasena(rs.getString("contrasena")); // Asigna contraseña encriptada
                    return u; // Devuelve el usuario encontrado
                }
            }
        } catch (SQLException e) { // Captura errores SQL
            e.printStackTrace(); // Imprime detalles del error para debugging
        }
        return null; // Retorna null si no se encontró usuario o hubo error
    }


    /*-------------------------- Sección: Búsquedas de usuario --------------------------*/

    /**
     * Busca un usuario en la base de datos por su nombre de usuario.
     * Retorna el usuario si existe, sino null.
     */
    @Override
    public Usuario buscarPorUsuario(String usuario) {
        String sql = "SELECT id, usuario, contrasena FROM usuarios WHERE usuario = ?";
        try (
                Connection conn = Conexion.getConnection(); // Obtiene conexión
                PreparedStatement ps = conn.prepareStatement(sql) // Prepara la consulta
        ) {
            ps.setString(1, usuario); // Asigna parámetro usuario

            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta
                if (rs.next()) { // Si encuentra un resultado
                    Usuario u = new Usuario(); // Instancia Usuario
                    u.setId(rs.getInt("id")); // Asigna ID
                    u.setUsuario(rs.getString("usuario")); // Asigna nombre
                    u.setContrasena(rs.getString("contrasena")); // Asigna contraseña encriptada
                    return u; // Retorna el usuario
                }
            }
        } catch (SQLException e) { // Captura excepción SQL
            e.printStackTrace(); // Imprime el error
        }
        return null; // Retorna null si no existe o error
    }

    /**
     * Busca un usuario en la base de datos por su ID.
     * Retorna el usuario si existe, sino null.
     */
    @Override
    public Usuario buscarPorId(int id) {
        String sql = "SELECT id, usuario, contrasena FROM usuarios WHERE id = ?";
        try (
                Connection conn = Conexion.getConnection(); // Obtiene conexión
                PreparedStatement ps = conn.prepareStatement(sql) // Prepara sentencia
        ) {
            ps.setInt(1, id); // Asigna parámetro id

            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta
                if (rs.next()) { // Si hay resultado
                    Usuario u = new Usuario(); // Crea instancia Usuario
                    u.setId(rs.getInt("id")); // Asigna id
                    u.setUsuario(rs.getString("usuario")); // Asigna nombre
                    u.setContrasena(rs.getString("contrasena")); // Asigna contraseña
                    return u; // Retorna usuario
                }
            }
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime error
        }
        return null; // Retorna null si no existe o error
    }


    /*-------------------------- Sección: Registro y actualización --------------------------*/

    /**
     * Inserta un nuevo usuario en la base de datos.
     * Retorna true si la inserción fue exitosa, false si hubo error.
     *
     * Si se modifica esta función, podría afectar el registro de nuevos usuarios
     * y la integridad de los datos guardados.
     */
    @Override
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (usuario, contrasena) VALUES (?, ?)";
        try (
                Connection conn = Conexion.getConnection(); // Abre conexión
                PreparedStatement ps = conn.prepareStatement(sql) // Prepara sentencia
        ) {
            ps.setString(1, usuario.getUsuario()); // Asigna nombre de usuario
            ps.setString(2, Encriptacion.sha1(usuario.getContrasena())); // Encripta y asigna contraseña

            int filas = ps.executeUpdate(); // Ejecuta inserción en BD
            return filas > 0; // Retorna true si se insertó al menos una fila
        } catch (SQLException e) { // Captura excepción SQL
            e.printStackTrace(); // Imprime error
        }
        return false; // Retorna false si hubo error
    }

    /**
     * Actualiza los datos (usuario y contraseña) de un usuario existente.
     * Retorna true si la actualización fue exitosa.
     *
     * Si se modifica esta función, puede afectar la capacidad
     * de los usuarios para cambiar su información y afectar la seguridad.
     */
    @Override
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET usuario = ?, contrasena = ? WHERE id = ?";
        try (
                Connection conn = Conexion.getConnection(); // Abre conexión
                PreparedStatement ps = conn.prepareStatement(sql) // Prepara sentencia
        ) {
            ps.setString(1, usuario.getUsuario()); // Asigna nuevo nombre de usuario
            ps.setString(2, Encriptacion.sha1(usuario.getContrasena())); // Encripta y asigna nueva contraseña
            ps.setInt(3, usuario.getId()); // Especifica qué usuario actualizar por id

            int filas = ps.executeUpdate(); // Ejecuta actualización en BD
            return filas > 0; // Retorna true si al menos una fila se actualizó
        } catch (SQLException e) { // Maneja error SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return false; // Retorna false si hubo error
    }


    /*-------------------------- Sección: Eliminación --------------------------*/

    /**
     * Elimina un usuario de la base de datos por su ID.
     * Retorna true si se eliminó correctamente.
     */
    @Override
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (
                Connection conn = Conexion.getConnection(); // Abre conexión
                PreparedStatement ps = conn.prepareStatement(sql) // Prepara sentencia
        ) {
            ps.setInt(1, id); // Asigna parámetro ID

            int filas = ps.executeUpdate(); // Ejecuta eliminación
            return filas > 0; // Retorna true si al menos un registro fue eliminado
        } catch (SQLException e) { // Captura error SQL
            e.printStackTrace(); // Imprime detalles
        }
        return false; // Retorna false si error
    }


    /*-------------------------- Sección: Obtener todos los usuarios --------------------------*/

    /**
     * Obtiene y retorna una lista con todos los usuarios de la base de datos.
     * Retorna lista vacía si no hay usuarios o hay error.
     */
    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>(); // Crea lista vacía para usuarios
        String sql = "SELECT id, usuario, contrasena FROM usuarios"; // Consulta para obtener todos los usuarios
        try (
                Connection conn = Conexion.getConnection(); // Abre conexión
                PreparedStatement ps = conn.prepareStatement(sql); // Prepara consulta
                ResultSet rs = ps.executeQuery() // Ejecuta consulta y obtiene resultados
        ) {
            while (rs.next()) { // Recorre cada fila del resultado
                Usuario u = new Usuario(); // Crea instancia de usuario
                u.setId(rs.getInt("id")); // Asigna ID
                u.setUsuario(rs.getString("usuario")); // Asigna nombre de usuario
                u.setContrasena(rs.getString("contrasena")); // Asigna contraseña encriptada
                lista.add(u); // Añade usuario a la lista
            }
        } catch (SQLException e) { // Captura errores SQL
            e.printStackTrace(); // Imprime detalles de error
        }
        return lista; // Retorna la lista (puede estar vacía)
    }
}
