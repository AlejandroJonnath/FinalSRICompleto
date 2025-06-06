package services;

import model.Categoria;
import model.Producto;
import repository.CategoriaRepository;
import repository.ProductRepository;

import java.util.List;

public class ProductService {
    private final ProductRepository repository = new ProductRepository();
    private final CategoriaRepository categoriaRepository = new CategoriaRepository();
    private final ProductRepository productRepository = new ProductRepository();

    public List<Categoria> listarCategorias() {
        return categoriaRepository.listarTodas();
    }

    public List<Producto> listarProductos() {
        // devuelve solo activos (activo = 1)
        return repository.listarTodos();
    }

    public Producto obtenerPorId(int id) {
        // solo activos
        return repository.obtenerPorId(id);
    }

    public boolean insertarProducto(Producto producto) {
        return repository.insertar(producto);
    }

    public boolean actualizarProducto(Producto producto) {
        return repository.actualizar(producto);
    }

    public void desactivarProducto(int id) {
        // soft‐delete: marca activo = 0
        repository.eliminar(id);
    }

    public boolean reducirStock(int productoId, int cantidad) {
        return repository.reducirStock(productoId, cantidad);
    }

    /**
     * Reactivar un producto previamente inactivado (activo = 0).
     */
    public void activarProducto(int id) {
        repository.activar(id);
    }

    /**
     * Lista todos los productos inactivos (activo = 0).
     */
    public List<Producto> listarProductosInactivos() {
        return repository.listarInactivos();
    }


}

