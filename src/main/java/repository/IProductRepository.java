package repository; // Define el paquete donde se ubica esta interfaz

import java.util.List; // Importa la interfaz List
import model.Producto; // Importa el modelo Producto

/**
 * Contrato para todas las operaciones CRUD de la tabla 'productos'.
 */
public interface IProductRepository {
    List<Producto> listarTodos(); // Devuelve una lista con todos los productos
    Producto obtenerPorId(int id); // Devuelve un producto según su ID
    boolean insertar(Producto producto); // Inserta un nuevo producto y devuelve true si tuvo éxito
    boolean actualizar(Producto producto); // Actualiza un producto existente y devuelve true si tuvo éxito
    boolean eliminar(int id); // Elimina (o desactiva) un producto por su ID y devuelve true si tuvo éxito
    boolean reducirStock(int productoId, int cantidad); // Reduce el stock de un producto y devuelve true si tuvo éxito
}
