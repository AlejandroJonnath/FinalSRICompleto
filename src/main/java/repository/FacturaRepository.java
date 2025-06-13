package repository;

import model.DetalleFactura;
import model.Factura;
import java.util.List;

public interface FacturaRepository {

    List<Factura> listarTodas();

    Factura obtenerFacturaPorId(int id);

    int insertar(Factura factura); // devuelve el id generado

    String obtenerSiguienteSecuencial(String establecimiento, String puntoEmision);

    List<DetalleFactura> obtenerDetallesPorFactura(int facturaId);

    // 🚀 Este método te falta:
    int obtenerUltimoFacturaId();
}