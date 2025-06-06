package services; // Paquete donde se ubica el servicio

import model.DetalleFactura; // Importa modelo de detalle de factura
import model.Factura; // Importa modelo de factura
import model.Producto; // Importa modelo producto (aunque no se usa directamente aquí)
import repository.IProductRepository; // Interfaz repositorio producto
import repository.ProductRepository; // Implementación del repositorio producto
import util.Conexion; // Utilidad para conexión JDBC

import java.math.BigDecimal; // Para manejo de valores decimales (monetarios)
import java.sql.*; // Clases JDBC: Connection, PreparedStatement, ResultSet, SQLException

public class InvoiceService { // Clase servicio para manejo de facturas

    private final IProductRepository productRepo = new ProductRepository(); // Instancia repositorio producto para operaciones relacionadas

    /**
     * Inserta una nueva factura y devuelve el ID generado.
     */
    public int insertarFactura(Factura factura) {
        String sql = "INSERT INTO facturas (cliente_nombre, cliente_cedula, total) VALUES (?, ?, ?)"; // SQL para insertar factura
        ResultSet rs = null; // ResultSet para obtener claves generadas
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Prepara la consulta para devolver ID

            ps.setString(1, factura.getClienteNombre()); // Param cliente_nombre
            ps.setString(2, factura.getClienteCedula()); // Param cliente_cedula
            ps.setBigDecimal(3, factura.getTotal()); // Param total (BigDecimal)

            int filas = ps.executeUpdate(); // Ejecuta inserción
            if (filas == 0) return -1; // Si no inserta, retorna -1

            rs = ps.getGeneratedKeys(); // Obtiene las claves generadas
            if (rs.next()) { // Si existe clave generada
                return rs.getInt(1); // Retorna el ID generado
            }
        } catch (SQLException e) { // Manejo de errores SQL
            e.printStackTrace(); // Imprime detalles del error
        } finally { // Cierra ResultSet en el bloque finally para asegurar cierre
            if (rs != null) try { rs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return -1; // Retorna -1 si hubo error o no se obtuvo ID
    }

    /**
     * Inserta un detalle en la tabla detalle_factura.
     */
    public boolean insertarDetalle(DetalleFactura detalle) {
        String sql = "INSERT INTO detalle_factura (factura_id, producto_id, cantidad, precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)"; // SQL para insertar detalle factura
        try (Connection conn = Conexion.getConnection(); // Abre conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara sentencia

            ps.setInt(1, detalle.getFacturaId()); // Param factura_id
            ps.setInt(2, detalle.getProductoId()); // Param producto_id
            ps.setInt(3, detalle.getCantidad()); // Param cantidad
            ps.setBigDecimal(4, detalle.getPrecioUnitario()); // Param precio_unitario
            ps.setBigDecimal(5, detalle.getSubtotal()); // Param subtotal

            int filas = ps.executeUpdate(); // Ejecuta inserción
            return filas > 0; // Retorna true si inserta al menos una fila
        } catch (SQLException e) { // Manejo errores SQL
            e.printStackTrace(); // Imprime error
        }
        return false; // Retorna false si falla
    }

    /**
     * Reduce el stock de un producto dado su ID.
     */
    public boolean reducirStock(int productoId, int cantidad) {
        return productRepo.reducirStock(productoId, cantidad); // Delegar en el repositorio la reducción de stock
    }
}
