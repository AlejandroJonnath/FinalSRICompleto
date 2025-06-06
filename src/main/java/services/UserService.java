
package services;

import model.Usuario;
import repository.IUserRepository;
import repository.UserRepository;

import java.util.List;

public class UserService {
    private final IUserRepository userRepo = new UserRepository();

    public Usuario validarUsuario(String usuario, String contrasena) {
        // Removemos la encriptación aquí porque ya se hace en el repositorio
        return userRepo.validarUsuario(usuario, contrasena);
    }

    public boolean registrarUsuario(Usuario nuevoUsuario) {
        Usuario existente = userRepo.buscarPorUsuario(nuevoUsuario.getUsuario());
        if (existente != null) {
            return false;
        }
        return userRepo.registrarUsuario(nuevoUsuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return userRepo.obtenerTodos();
    }

    public Usuario buscarPorId(int id) {
        return userRepo.buscarPorId(id);
    }

    public boolean actualizarUsuario(Usuario u) {
        return userRepo.actualizarUsuario(u);
    }

    public boolean eliminarUsuarioPorId(int id) {
        return userRepo.eliminarUsuario(id);
    }
}