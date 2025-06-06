package services;

import model.Usuario;
import repository.IUserRepository;
import repository.UserRepository;

import java.util.List;

public class UserService {
    private final IUserRepository userRepo = new UserRepository();

    /**
     * Valida un usuario con su nombre y contraseña.
     * La encriptación ya se maneja en el repositorio, por eso no se hace aquí.
     *
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña en texto plano
     * @return Usuario si es válido, null si no
     */
    public Usuario validarUsuario(String usuario, String contrasena) {
        return userRepo.validarUsuario(usuario, contrasena);
    }

    /**
     * Registra un nuevo usuario si no existe uno con el mismo nombre.
     *
     * @param nuevoUsuario Objeto usuario a registrar
     * @return true si se registró correctamente, false si el usuario ya existe
     */
    public boolean registrarUsuario(Usuario nuevoUsuario) {
        Usuario existente = userRepo.buscarPorUsuario(nuevoUsuario.getUsuario());
        if (existente != null) {
            return false; // Ya existe un usuario con ese nombre
        }
        return userRepo.registrarUsuario(nuevoUsuario);
    }

    /**
     * Obtiene la lista de todos los usuarios.
     *
     * @return lista de usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return userRepo.obtenerTodos();
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id ID del usuario
     * @return Usuario encontrado o null si no existe
     */
    public Usuario buscarPorId(int id) {
        return userRepo.buscarPorId(id);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param u Usuario con datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarUsuario(Usuario u) {
        return userRepo.actualizarUsuario(u);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarUsuarioPorId(int id) {
        return userRepo.eliminarUsuario(id);
    }
}
