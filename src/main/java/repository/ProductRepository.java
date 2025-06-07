package repository; // Define el paquete donde se ubica este repositorio

import model.Categoria; // Importa el modelo Categoria
import model.Producto; // Importa el modelo Producto
import util.Conexion; // Importa la utilidad para obtener conexiones JDBC

import java.sql.*; // Importa clases JDBC necesarias (Connection, PreparedStatement, ResultSet, SQLException)
import java.util.ArrayList; // Importa ArrayList para manejo de listas dinámicas
import java.util.List; // Importa la interfaz List para listas generales

// Implementa el contrato CRUD para productos, con métodos para listar, insertar, actualizar, eliminar y más
public class ProductRepository implements IProductRepository {

    /**
     * Listar todos los productos activos, junto con su categoría.
     * IMPORTANTE: Solo devuelve productos con activo = true (1).
     * Impacto si se modifica: Cambiar el filtro activo podría mostrar productos inactivos o causar inconsistencias en la UI.
     */
    @Override
    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>(); // Lista para guardar los productos recuperados
        // Consulta SQL para obtener productos activos y su categoría relacionada
        String sql = "SELECT p.id, p.nombre, p.marca, p.precio, p.stock, p.activo, " +
                "c.id AS categoria_id, c.nombre AS categoria_nombre " +
                "FROM productos p " +
                "JOIN categorias c ON p.categoria_id = c.id " +
                "WHERE p.activo = 1 " +
                "ORDER BY p.nombre, p.id";

        // Uso de try-with-resources para cerrar automáticamente conexión, statement y resultset
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Itera todas las filas del resultado
            while (rs.next()) {
                Categoria cat = new Categoria(); // Instancia de categoría
                cat.setId(rs.getInt("categoria_id")); // Asigna ID
                cat.setNombre(rs.getString("categoria_nombre")); // Asigna nombre

                Producto p = new Producto(); // Instancia producto
                p.setId(rs.getInt("id")); // ID producto
                p.setNombre(rs.getString("nombre")); // Nombre
                p.setMarca(rs.getString("marca")); // Marca
                p.setPrecio(rs.getBigDecimal("precio")); // Precio decimal
                p.setStock(rs.getInt("stock")); // Stock disponible
                p.setActivo(rs.getBoolean("activo")); // Estado activo
                p.setCategoria(cat); // Asocia categoría al producto

                lista.add(p); // Añade a la lista final
            }
        } catch (SQLException e) {
            e.printStackTrace(); // En caso de error SQL, imprime traza para depuración
        }
        return lista; // Retorna la lista completa de productos activos
    }

    /**
     * Obtiene un producto activo por su ID.
     * IMPORTANTE: Solo productos activos (activo=1).
     * Impacto: Si el filtro activo se elimina, podría devolver productos inactivos.
     */
    @Override
    public Producto obtenerPorId(int id) {
        String sql = "SELECT p.id, p.nombre, p.marca, p.precio, p.stock, p.activo, " +
                "c.id AS categoria_id, c.nombre AS categoria_nombre " +
                "FROM productos p " +
                "JOIN categorias c ON p.categoria_id = c.id " +
                "WHERE p.id = ? AND p.activo = 1"; // Parámetro para buscar por ID

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); // Asigna el parámetro id

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // Si encuentra producto
                    Categoria cat = new Categoria();
                    cat.setId(rs.getInt("categoria_id"));
                    cat.setNombre(rs.getString("categoria_nombre"));

                    Producto p = new Producto();
                    p.setId(rs.getInt("id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setMarca(rs.getString("marca"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setStock(rs.getInt("stock"));
                    p.setActivo(rs.getBoolean("activo"));
                    p.setCategoria(cat);

                    return p; // Retorna producto encontrado
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime error si falla consulta
        }
        return null; // Retorna null si no encuentra o error
    }

    /**
     * Inserta un nuevo producto en la base de datos.
     * No asigna estado activo explícitamente, asume por defecto activo=1 en BD.
     * Impacto: Cambiar la consulta puede afectar integridad de datos.
     */
    @Override
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO productos (nombre, marca, precio, stock, categoria_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getMarca());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setInt(5, producto.getCategoria().getId());

            int filas = ps.executeUpdate(); // Ejecuta la inserción
            return filas > 0; // True si inserta al menos un registro
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // False si ocurre error
    }

    /**
     * Actualiza un producto activo según su ID.
     * Solo actualiza productos activos (activo=1).
     * Impacto: Cambiar condición WHERE puede afectar productos inactivos o registros inexistentes.
     */
    @Override
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, marca = ?, precio = ?, stock = ?, categoria_id = ? " +
                "WHERE id = ? AND activo = 1";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getMarca());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setInt(5, producto.getCategoria().getId());
            ps.setInt(6, producto.getId());

            int filas = ps.executeUpdate(); // Ejecuta actualización
            return filas > 0; // True si modificó al menos un registro
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * "Elimina" un producto cambiando su estado activo a 0 (soft delete).
     * Impacto: No elimina físicamente la fila, por lo que el producto puede reactivarse.
     */
    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE productos SET activo = 0 WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Reduce el stock de un producto, solo si hay suficiente stock disponible y el producto está activo.
     * IMPORTANTE: La consulta garantiza que stock no quede negativo.
     * Impacto: Usar esta función evita inconsistencias en stock y evita ventas con stock insuficiente.
     */
    @Override
    public boolean reducirStock(int productoId, int cantidad) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ? AND activo = 1";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, productoId);
            ps.setInt(3, cantidad);

            int filas = ps.executeUpdate();
            return filas > 0; // True si descontó el stock correctamente
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Reactiva un producto que esté inactivo (activo = 0).
     * Impacto: Permite "deshacer" la eliminación lógica sin perder datos.
     */
    public boolean activar(int id) {
        String sql = "UPDATE productos SET activo = 1 WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lista productos inactivos.
     * Impacto: Útil para mostrar productos "eliminados" y permitir reactivarlos o revisarlos.
     */
    public List<Producto> listarInactivos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.marca, p.precio, p.stock, p.activo, " +
                "c.id AS categoria_id, c.nombre AS categoria_nombre " +
                "FROM productos p " +
                "JOIN categorias c ON p.categoria_id = c.id " +
                "WHERE p.activo = 0 " +
                "ORDER BY p.nombre, p.id";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setId(rs.getInt("categoria_id"));
                cat.setNombre(rs.getString("categoria_nombre"));

                Producto p = new Producto();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setMarca(rs.getString("marca"));
                p.setPrecio(rs.getBigDecimal("precio"));
                p.setStock(rs.getInt("stock"));
                p.setActivo(rs.getBoolean("activo")); // false en este caso
                p.setCategoria(cat);

                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
