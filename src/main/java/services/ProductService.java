package services;

import model.Categoria;            // Modelo para categorías
import model.Producto;             // Modelo para productos
import repository.CategoriaRepository; // Repositorio para categorías
import repository.ProductRepository;   // Repositorio para productos

import java.util.List;             // Para usar listas

/**
 * Sección: Clase ProductService
 * Esta clase actúa como capa de servicio que maneja la lógica de negocio
 * relacionada con productos y categorías. Utiliza los repositorios para
 * acceder y modificar datos.
 */
public class ProductService {

    // Repositorio para operaciones CRUD de productos
    private final ProductRepository repository = new ProductRepository();

    // Repositorio para operaciones CRUD de categorías
    private final CategoriaRepository categoriaRepository = new CategoriaRepository();


    /**
     * Sección: Método listarCategorias
     * Retorna una lista con todas las categorías disponibles.
     *
     * @return lista de categorías
     */
    public List<Categoria> listarCategorias() {
        // Delegamos al repositorio la obtención de todas las categorías
        return categoriaRepository.listarTodas();
    }

    /**
     * Sección: Método listarProductos
     * Retorna todos los productos activos (activo = 1).
     *
     * @return lista de productos activos
     */
    public List<Producto> listarProductos() {
        // Obtiene solo productos activos desde el repositorio
        return repository.listarTodos();
    }

    /**
     * Sección: Método obtenerPorId
     * Busca un producto activo por su ID.
     *
     * @param id Identificador del producto
     * @return producto encontrado o null si no existe
     */
    public Producto obtenerPorId(int id) {
        // Obtiene un producto específico siempre que esté activo
        return repository.obtenerPorId(id);
    }

    /**
     * Sección: Método insertarProducto
     * Inserta un nuevo producto en la base de datos.
     *
     * @param producto Objeto producto a insertar
     * @return true si la inserción fue exitosa
     */
    public boolean insertarProducto(Producto producto) {
        // Llama al repositorio para insertar el producto
        return repository.insertar(producto);
    }

    /**
     * Sección: Método actualizarProducto
     * Actualiza los datos de un producto existente.
     *
     * @param producto Objeto producto con datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarProducto(Producto producto) {
        // Llama al repositorio para actualizar el producto
        return repository.actualizar(producto);
    }

    /**
     * Sección: Método desactivarProducto
     * Desactiva un producto mediante soft-delete (marca activo = 0).
     *
     * @param id ID del producto a desactivar
     */
    public void desactivarProducto(int id) {
        // Cambia el estado del producto a inactivo en el repositorio
        repository.eliminar(id);
    }

    /**
     * Sección: Método reducirStock
     * Reduce el stock de un producto dado su ID y cantidad.
     *
     * @param productoId ID del producto
     * @param cantidad Cantidad a reducir
     * @return true si el stock fue reducido correctamente
     */
    public boolean reducirStock(int productoId, int cantidad) {
        // Llama al repositorio para disminuir el stock del producto
        return repository.reducirStock(productoId, cantidad);
    }

    /**
     * Sección: Método activarProducto
     * Reactiva un producto previamente desactivado (activo = 0).
     *
     * @param id ID del producto a activar
     */
    public void activarProducto(int id) {
        // Cambia el estado del producto a activo en el repositorio
        repository.activar(id);
    }

    /**
     * Sección: Método listarProductosInactivos
     * Retorna todos los productos que están inactivos (activo = 0).
     *
     * @return lista de productos inactivos
     */
    public List<Producto> listarProductosInactivos() {
        // Obtiene productos marcados como inactivos
        return repository.listarInactivos();
    }

    /*
     * Si se modifica cualquiera de estos métodos,
     * se afectará la forma en que la capa de negocio
     * interactúa con los datos, pudiendo romper la lógica
     * de negocio o la integridad de los productos y categorías.
     */
}
