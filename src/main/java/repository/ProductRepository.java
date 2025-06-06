package repository; // Define el paquete donde se ubica este repositorio

import model.Categoria; // Importa el modelo Categoria
import model.Producto; // Importa el modelo Producto
import util.Conexion; // Importa la utilidad para obtener conexiones JDBC

import java.sql.*; // Importa clases JDBC: Connection, PreparedStatement, ResultSet, SQLException
import java.util.ArrayList; // Importa ArrayList para colecciones dinámicas
import java.util.List; // Importa la interfaz List

public class ProductRepository implements IProductRepository { // Clase que implementa el contrato CRUD de productos

    @Override
    public List<Producto> listarTodos() { // Método que devuelve todos los productos activos
        List<Producto> lista = new ArrayList<>(); // Crea la lista de resultados
        String sql = "SELECT p.id, p.nombre, p.marca, p.precio, p.stock, p.activo, " +
                "c.id AS categoria_id, c.nombre AS categoria_nombre " +
                "FROM productos p " +
                "JOIN categorias c ON p.categoria_id = c.id " +
                "WHERE p.activo = 1 " +
                "ORDER BY p.nombre, p.id";
        // Consulta SQL para productos activos con su categoría

        try (Connection conn = Conexion.getConnection(); // Abre conexión a BD
             PreparedStatement ps = conn.prepareStatement(sql); // Prepara la consulta
             ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y obtiene el resultado

            while (rs.next()) { // Itera cada fila del ResultSet
                Categoria cat = new Categoria(); // Crea objeto Categoria
                cat.setId(rs.getInt("categoria_id")); // Asigna ID de categoría
                cat.setNombre(rs.getString("categoria_nombre")); // Asigna nombre de categoría

                Producto p = new Producto(); // Crea objeto Producto
                p.setId(rs.getInt("id")); // Asigna ID de producto
                p.setNombre(rs.getString("nombre")); // Asigna nombre de producto
                p.setMarca(rs.getString("marca")); // Asigna marca de producto
                p.setPrecio(rs.getBigDecimal("precio")); // Asigna precio del producto
                p.setStock(rs.getInt("stock")); // Asigna stock del producto
                p.setActivo(rs.getBoolean("activo")); // Asigna estado activo
                p.setCategoria(cat); // Asocia la categoría al producto

                lista.add(p); // Agrega el producto a la lista
            }
        } catch (SQLException e) { // Maneja excepciones SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return lista; // Devuelve la lista de productos
    }

    @Override
    public Producto obtenerPorId(int id) { // Método para obtener un producto activo por su ID
        String sql = "SELECT p.id, p.nombre, p.marca, p.precio, p.stock, p.activo, " +
                "c.id AS categoria_id, c.nombre AS categoria_nombre " +
                "FROM productos p " +
                "JOIN categorias c ON p.categoria_id = c.id " +
                "WHERE p.id = ? AND p.activo = 1"; // Consulta SQL con parámetro ID

        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la consulta
            ps.setInt(1, id); // Asigna el parámetro ID
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta y obtiene ResultSet
                if (rs.next()) { // Si existe fila
                    Categoria cat = new Categoria(); // Crea categoría
                    cat.setId(rs.getInt("categoria_id")); // Asigna ID categoría
                    cat.setNombre(rs.getString("categoria_nombre")); // Asigna nombre categoría

                    Producto p = new Producto(); // Crea producto
                    p.setId(rs.getInt("id")); // Asigna ID producto
                    p.setNombre(rs.getString("nombre")); // Asigna nombre
                    p.setMarca(rs.getString("marca")); // Asigna marca
                    p.setPrecio(rs.getBigDecimal("precio")); // Asigna precio
                    p.setStock(rs.getInt("stock")); // Asigna stock
                    p.setActivo(rs.getBoolean("activo")); // Asigna estado activo
                    p.setCategoria(cat); // Asocia categoría

                    return p; // Retorna el producto encontrado
                }
            }
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return null; // Retorna null si no se encontró o hubo error
    }

    @Override
    public boolean insertar(Producto producto) { // Método para insertar un nuevo producto
        String sql = "INSERT INTO productos (nombre, marca, precio, stock, categoria_id) VALUES (?, ?, ?, ?, ?)"; // SQL de inserción
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la sentencia
            ps.setString(1, producto.getNombre()); // Asigna nombre
            ps.setString(2, producto.getMarca()); // Asigna marca
            ps.setBigDecimal(3, producto.getPrecio()); // Asigna precio
            ps.setInt(4, producto.getStock()); // Asigna stock
            ps.setInt(5, producto.getCategoria().getId()); // Asigna ID de categoría

            int filas = ps.executeUpdate(); // Ejecuta la actualización
            return filas > 0; // Retorna true si se insertó al menos una fila
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return false; // Retorna false si hubo error
    }

    @Override
    public boolean actualizar(Producto producto) { // Método para actualizar un producto activo
        String sql = "UPDATE productos SET nombre = ?, marca = ?, precio = ?, stock = ?, categoria_id = ? " +
                "WHERE id = ? AND activo = 1"; // SQL de actualización con condición activo
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la sentencia
            ps.setString(1, producto.getNombre()); // Asigna nombre
            ps.setString(2, producto.getMarca()); // Asigna marca
            ps.setBigDecimal(3, producto.getPrecio()); // Asigna precio
            ps.setInt(4, producto.getStock()); // Asigna stock
            ps.setInt(5, producto.getCategoria().getId()); // Asigna categoría
            ps.setInt(6, producto.getId()); // Asigna ID para la cláusula WHERE

            int filas = ps.executeUpdate(); // Ejecuta la actualización
            return filas > 0; // Retorna true si al menos una fila fue afectada
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return false; // Retorna false si hubo error
    }

    @Override
    public boolean eliminar(int id) { // Método para "eliminar" (desactivar) un producto
        String sql = "UPDATE productos SET activo = 0 WHERE id = ?"; // SQL de soft‐delete
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la sentencia
            ps.setInt(1, id); // Asigna ID del producto
            int filas = ps.executeUpdate(); // Ejecuta la actualización
            return filas > 0; // Retorna true si al menos una fila fue afectada
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return false; // Retorna false si hubo error
    }

    @Override
    public boolean reducirStock(int productoId, int cantidad) { // Método para descontar stock si hay suficiente
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ? AND activo = 1"; // SQL con condiciones
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la sentencia
            ps.setInt(1, cantidad); // Asigna cantidad a descontar
            ps.setInt(2, productoId); // Asigna ID del producto
            ps.setInt(3, cantidad); // Verifica que stock actual sea >= cantidad

            int filas = ps.executeUpdate(); // Ejecuta la actualización
            return filas > 0; // Retorna true si se descontó stock
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return false; // Retorna false si hubo error
    }

    public boolean activar(int id) { // Método para reactivar un producto inactivo
        String sql = "UPDATE productos SET activo = 1 WHERE id = ?"; // SQL para activar
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la sentencia
            ps.setInt(1, id); // Asigna ID del producto
            int filas = ps.executeUpdate(); // Ejecuta la actualización
            return filas > 0; // Retorna true si se actualizó
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return false; // Retorna false si hubo error
    }

    public List<Producto> listarInactivos() { // Método que devuelve productos inactivos
        List<Producto> lista = new ArrayList<>(); // Crea la lista de resultados
        String sql = "SELECT p.id, p.nombre, p.marca, p.precio, p.stock, p.activo, " +
                "c.id AS categoria_id, c.nombre AS categoria_nombre " +
                "FROM productos p " +
                "JOIN categorias c ON p.categoria_id = c.id " +
                "WHERE p.activo = 0 " +
                "ORDER BY p.nombre, p.id";// Consulta para productos inactivos

        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql); // Prepara la consulta
             ResultSet rs = ps.executeQuery()) { // Ejecuta y obtiene ResultSet

            while (rs.next()) { // Itera cada fila
                Categoria cat = new Categoria(); // Crea categoría
                cat.setId(rs.getInt("categoria_id")); // Asigna ID categoría
                cat.setNombre(rs.getString("categoria_nombre")); // Asigna nombre categoría

                Producto p = new Producto(); // Crea producto
                p.setId(rs.getInt("id")); // Asigna ID
                p.setNombre(rs.getString("nombre")); // Asigna nombre
                p.setMarca(rs.getString("marca")); // Asigna marca
                p.setPrecio(rs.getBigDecimal("precio")); // Asigna precio
                p.setStock(rs.getInt("stock")); // Asigna stock
                p.setActivo(rs.getBoolean("activo")); // Asigna estado activo (falso)
                p.setCategoria(cat); // Asocia categoría

                lista.add(p); // Añade el producto a la lista
            }
        } catch (SQLException e) { // Maneja errores SQL
            e.printStackTrace(); // Imprime detalles del error
        }
        return lista; // Devuelve la lista de productos inactivos
    }
}
