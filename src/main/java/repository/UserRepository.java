package repository; // Define el paquete donde se ubica este repositorio

import model.Usuario; // Importa el modelo Usuario
import util.Conexion; // Importa la utilidad para obtener conexiones JDBC
import util.Encriptacion; // Importa la utilería para encriptar contraseñas

import java.sql.*; // Importa clases JDBC: Connection, PreparedStatement, ResultSet, SQLException
import java.util.ArrayList; // Importa ArrayList para colecciones dinámicas
import java.util.List; // Importa la interfaz List

public class UserRepository implements IUserRepository { // Implementa la interfaz de operaciones sobre usuarios

    @Override
    public Usuario validarUsuario(String usuario, String contrasena) { // Valida credenciales y devuelve el Usuario si coinciden
        String sql = "SELECT id, usuario, contrasena FROM usuarios WHERE usuario = ? AND contrasena = ?"; // Consulta SQL parametrizada
        try (Connection conn = Conexion.getConnection(); // Abre conexión a la BD
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la consulta

            ps.setString(1, usuario); // Asigna el parámetro usuario
            ps.setString(2, Encriptacion.sha1(contrasena)); // Asigna la contraseña encriptada

            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y obtiene resultados
                if (rs.next()) { // Si encuentra un registro
                    Usuario u = new Usuario(); // Crea instancia de Usuario
                    u.setId(rs.getInt("id")); // Asigna el ID
                    u.setUsuario(rs.getString("usuario")); // Asigna el nombre de usuario
                    u.setContrasena(rs.getString("contrasena")); // Asigna la contraseña encriptada
                    return u; // Devuelve el usuario encontrado
                }
            }
        } catch (SQLException e) { // Maneja errores de SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return null; // Retorna null si no valida o hay error
    }

    @Override
    public Usuario buscarPorUsuario(String usuario) { // Busca un usuario por nombre de usuario
        String sql = "SELECT id, usuario, contrasena FROM usuarios WHERE usuario = ?"; // Consulta SQL
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la consulta

            ps.setString(1, usuario); // Asigna el parámetro usuario
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta
                if (rs.next()) { // Si hay resultado
                    Usuario u = new Usuario(); // Crea instancia de Usuario
                    u.setId(rs.getInt("id")); // Asigna ID
                    u.setUsuario(rs.getString("usuario")); // Asigna nombre
                    u.setContrasena(rs.getString("contrasena")); // Asigna contraseña
                    return u; // Retorna el usuario encontrado
                }
            }
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime el error
        }
        return null; // Retorna null si no encuentra o hay error
    }

    @Override
    public boolean registrarUsuario(Usuario usuario) { // Registra un nuevo usuario en la BD
        String sql = "INSERT INTO usuarios (usuario, contrasena) VALUES (?, ?)"; // SQL de inserción
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la sentencia

            ps.setString(1, usuario.getUsuario()); // Asigna nombre de usuario
            ps.setString(2, Encriptacion.sha1(usuario.getContrasena())); // Asigna contraseña encriptada

            int filas = ps.executeUpdate(); // Ejecuta inserción
            return filas > 0; // Retorna true si al menos una fila se insertó
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return false; // Retorna false si hubo error
    }

    @Override
    public List<Usuario> obtenerTodos() { // Devuelve todos los usuarios de la tabla
        List<Usuario> lista = new ArrayList<>(); // Crea lista para resultados
        String sql = "SELECT id, usuario, contrasena FROM usuarios"; // Consulta SQL
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql); // Prepara la consulta
             ResultSet rs = ps.executeQuery()) { // Ejecuta y obtiene ResultSet

            while (rs.next()) { // Itera cada fila
                Usuario u = new Usuario(); // Crea instancia de Usuario
                u.setId(rs.getInt("id")); // Asigna ID
                u.setUsuario(rs.getString("usuario")); // Asigna nombre
                u.setContrasena(rs.getString("contrasena")); // Asigna contraseña
                lista.add(u); // Agrega a la lista
            }
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return lista; // Retorna la lista de usuarios
    }

    @Override
    public Usuario buscarPorId(int id) { // Busca un usuario por su ID
        String sql = "SELECT id, usuario, contrasena FROM usuarios WHERE id = ?"; // Consulta SQL
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara sentencia

            ps.setInt(1, id); // Asigna el parámetro ID
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta
                if (rs.next()) { // Si hay resultado
                    Usuario u = new Usuario(); // Crea usuario
                    u.setId(rs.getInt("id")); // Asigna ID
                    u.setUsuario(rs.getString("usuario")); // Asigna nombre
                    u.setContrasena(rs.getString("contrasena")); // Asigna contraseña
                    return u; // Retorna el usuario
                }
            }
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime el error
        }
        return null; // Retorna null si no existe o hay error
    }

    @Override
    public boolean actualizarUsuario(Usuario usuario) { // Actualiza nombre y contraseña de un usuario existente
        String sql = "UPDATE usuarios SET usuario = ?, contrasena = ? WHERE id = ?"; // SQL de actualización
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara sentencia

            ps.setString(1, usuario.getUsuario()); // Asigna nuevo nombre
            ps.setString(2, Encriptacion.sha1(usuario.getContrasena())); // Asigna nueva contraseña encriptada
            ps.setInt(3, usuario.getId()); // Asigna ID en cláusula WHERE

            int filas = ps.executeUpdate(); // Ejecuta actualización
            return filas > 0; // Retorna true si al menos una fila fue afectada
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return false; // Retorna false si hubo error
    }

    @Override
    public boolean eliminarUsuario(int id) { // Elimina un usuario por ID
        String sql = "DELETE FROM usuarios WHERE id = ?"; // SQL de eliminación
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara sentencia

            ps.setInt(1, id); // Asigna ID al parámetro
            int filas = ps.executeUpdate(); // Ejecuta eliminación
            return filas > 0; // Retorna true si se eliminó al menos un registro
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return false; // Retorna false si hubo error
    }
}
