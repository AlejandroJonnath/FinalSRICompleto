package services;

import model.Usuario;
import repository.IUserRepository;
import repository.UserRepository;

import java.util.List;

/**
 * Sección: Clase UserService
 * Esta clase maneja la lógica de negocio relacionada con los usuarios.
 * Usa el repositorio IUserRepository para acceder y modificar los datos.
 */
public class UserService {

    // Instancia del repositorio para manejar usuarios (interfaz y clase concreta)
    private final IUserRepository userRepo = new UserRepository();

    /**
     * Sección: Método validarUsuario
     * Valida las credenciales de un usuario (usuario y contraseña).
     * La encriptación de la contraseña se maneja en el repositorio.
     *
     * @param usuario Nombre de usuario en texto plano
     * @param contrasena Contraseña en texto plano
     * @return Usuario si las credenciales son válidas, null si no
     */
    public Usuario validarUsuario(String usuario, String contrasena) {
        // Llama al repositorio para validar el usuario y obtener el objeto Usuario
        return userRepo.validarUsuario(usuario, contrasena);
    }

    /**
     * Sección: Método registrarUsuario
     * Registra un nuevo usuario solo si no existe otro con el mismo nombre.
     *
     * @param nuevoUsuario Objeto Usuario con los datos a registrar
     * @return true si se registró exitosamente, false si ya existe un usuario con ese nombre
     */
    public boolean registrarUsuario(Usuario nuevoUsuario) {
        // Busca si ya existe un usuario con el mismo nombre
        Usuario existente = userRepo.buscarPorUsuario(nuevoUsuario.getUsuario());

        if (existente != null) {
            // Si ya existe, no registra y retorna falso
            return false;
        }

        // Si no existe, llama al repositorio para registrar el nuevo usuario
        return userRepo.registrarUsuario(nuevoUsuario);
    }

    /**
     * Sección: Método obtenerTodosLosUsuarios
     * Obtiene la lista completa de usuarios registrados.
     *
     * @return lista de todos los usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        // Obtiene todos los usuarios desde el repositorio
        return userRepo.obtenerTodos();
    }

    /**
     * Sección: Método buscarPorId
     * Busca un usuario por su ID único.
     *
     * @param id ID del usuario
     * @return Usuario encontrado o null si no existe
     */
    public Usuario buscarPorId(int id) {
        // Busca el usuario por su ID en el repositorio
        return userRepo.buscarPorId(id);
    }

    /**
     * Sección: Método actualizarUsuario
     * Actualiza la información de un usuario existente.
     *
     * @param u Objeto Usuario con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarUsuario(Usuario u) {
        // Llama al repositorio para actualizar los datos del usuario
        return userRepo.actualizarUsuario(u);
    }

    /**
     * Sección: Método eliminarUsuarioPorId
     * Elimina un usuario identificado por su ID.
     *
     * @param id ID del usuario a eliminar
     * @return true si la eliminación fue exitosa, false si hubo error o no existe
     */
    public boolean eliminarUsuarioPorId(int id) {
        // Llama al repositorio para eliminar el usuario
        return userRepo.eliminarUsuario(id);
    }

    /*
     * Impacto de modificaciones:
     * Cambiar estos métodos puede afectar la lógica de validación,
     * registro, actualización o eliminación de usuarios,
     * por lo que debe hacerse con cuidado para no romper la integridad de los datos ni la seguridad.
     */
}
