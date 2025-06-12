package services;

import model.DetalleFactura;
import model.Factura;
import repository.FacturaRepository;
import repository.FacturaRepositoryImple;
import repository.IProductRepository;
import repository.ProductRepository;
import util.Conexion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InvoiceService {

    private final IProductRepository productRepo = new ProductRepository();
    private final FacturaRepository facturaRepo = new FacturaRepositoryImple();

    /**
     * Inserta una factura delegando al FacturaRepository.
     */
    public int insertarFactura(Factura factura) {
        return facturaRepo.insertar(factura);
    }

    /**
     * Obtiene siguiente secuencial delegando al FacturaRepository.
     */
    public String obtenerSiguienteSecuencial(String establecimiento, String puntoEmision) {
        return facturaRepo.obtenerSiguienteSecuencial(establecimiento, puntoEmision);
    }

    /**
     * Inserta un detalle completo en la tabla detalle_factura.
     */
    public boolean insertarDetalle(DetalleFactura detalle) {
        String sql = "INSERT INTO detalle_factura (factura_id, producto_id, cantidad, precio_unitario, subtotal, stock, descuento, iva) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detalle.getFactura().getId());
            ps.setInt(2, detalle.getProducto().getId());
            ps.setInt(3, detalle.getCantidad());
            ps.setBigDecimal(4, detalle.getPrecioUnitario());
            ps.setBigDecimal(5, detalle.getSubtotal());
            ps.setInt(6, detalle.getStock());
            ps.setBigDecimal(7, detalle.getDescuento());
            ps.setBigDecimal(8, detalle.getIva()); // porcentaje: 12.00 o 15.00

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Reduce el stock de un producto dado su ID.
     */
    public boolean reducirStock(int productoId, int cantidad) {
        return productRepo.reducirStock(productoId, cantidad);
    }
}
