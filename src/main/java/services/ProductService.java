package services;

import model.Categoria;            // Modelo para categorías
import model.Producto;             // Modelo para productos
import repository.CategoriaRepository; // Repositorio para categorías
import repository.ProductRepository;   // Repositorio para productos

import java.util.List;             // Para usar listas

public class ProductService {     // Servicio para manejar lógica de productos y categorías

    private final ProductRepository repository = new ProductRepository(); // Repositorio para productos
    private final CategoriaRepository categoriaRepository = new CategoriaRepository(); // Repositorio para categorías


    /**
     * Lista todas las categorías disponibles.
     * @return lista de categorías
     */
    public List<Categoria> listarCategorias() {
        return categoriaRepository.listarTodas(); // Llama a repositorio para obtener todas las categorías
    }

    /**
     * Lista todos los productos activos (activo = 1).
     * @return lista de productos activos
     */
    public List<Producto> listarProductos() {
        // devuelve solo productos activos
        return repository.listarTodos();
    }

    /**
     * Obtiene un producto activo por su ID.
     * @param id Identificador del producto
     * @return producto encontrado o null si no existe
     */
    public Producto obtenerPorId(int id) {
        // devuelve producto solo si está activo
        return repository.obtenerPorId(id);
    }

    /**
     * Inserta un nuevo producto.
     * @param producto Objeto producto a insertar
     * @return true si se inserta correctamente
     */
    public boolean insertarProducto(Producto producto) {
        return repository.insertar(producto);
    }

    /**
     * Actualiza un producto existente.
     * @param producto Objeto producto con datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarProducto(Producto producto) {
        return repository.actualizar(producto);
    }

    /**
     * Desactiva un producto mediante soft-delete (activo = 0).
     * @param id ID del producto a desactivar
     */
    public void desactivarProducto(int id) {
        repository.eliminar(id); // En realidad cambia el campo activo a 0
    }

    /**
     * Reduce el stock de un producto dado su ID y cantidad.
     * @param productoId ID del producto
     * @param cantidad Cantidad a reducir
     * @return true si el stock fue reducido correctamente
     */
    public boolean reducirStock(int productoId, int cantidad) {
        return repository.reducirStock(productoId, cantidad);
    }

    /**
     * Reactiva un producto previamente desactivado (activo = 0).
     * @param id ID del producto a activar
     */
    public void activarProducto(int id) {
        repository.activar(id);
    }

    /**
     * Lista todos los productos inactivos (activo = 0).
     * @return lista de productos inactivos
     */
    public List<Producto> listarProductosInactivos() {
        return repository.listarInactivos();
    }

}
