package repository; // Define el paquete donde se ubica esta interfaz

import model.Usuario; // Importa el modelo Usuario
import java.util.List; // Importa la interfaz List

public interface IUserRepository { // Declara la interfaz para operaciones sobre usuarios
    Usuario validarUsuario(String usuario, String contrasena); // Método para validar credenciales, devuelve Usuario o null

    Usuario buscarPorUsuario(String usuario); // Método para buscar usuario por nombre de usuario

    boolean registrarUsuario(Usuario usuario); // Método para registrar un nuevo usuario, devuelve true si tiene éxito

    // ===========================
    // NUEVOS MÉTODOS PARA CRUD
    // ===========================
    /**
     * Retorna todos los usuarios de la tabla 'usuarios'.
     */
    List<Usuario> obtenerTodos(); // Devuelve una lista con todos los usuarios

    /**
     * Busca un usuario por su ID.
     * Devuelve el Usuario si existe, o null si no existe.
     */
    Usuario buscarPorId(int id); // Método para obtener un usuario por su ID

    /**
     * Actualiza un usuario existente (nombre + contraseña).
     * Devuelve true si se actualizó correctamente.
     */
    boolean actualizarUsuario(Usuario usuario); // Método para actualizar datos de un usuario, devuelve true si tuvo éxito

    /**
     * Elimina (o desactiva) un usuario por su ID.
     * Devuelve true si se eliminó/desactivó correctamente.
     */
    boolean eliminarUsuario(int id); // Método para eliminar o desactivar un usuario por ID, devuelve true si tuvo éxito
}
