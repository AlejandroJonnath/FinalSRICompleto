package repository;

import model.Factura;
import java.util.List;

public interface FacturaRepository {

    List<Factura> listarTodas();

    Factura obtenerPorId(int id);

    int insertar(Factura factura); // devuelve el id generado

    String obtenerSiguienteSecuencial(String establecimiento, String puntoEmision);
}