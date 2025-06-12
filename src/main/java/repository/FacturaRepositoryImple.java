package repository;

import model.Factura;
import util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaRepositoryImple implements FacturaRepository {

    @Override
    public List<Factura> listarTodas() {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT * FROM factura";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Factura f = new Factura();
                f.setId(rs.getInt("id"));
                f.setFecha(rs.getTimestamp("fecha"));
                f.setClienteNombre(rs.getString("cliente_nombre"));
                f.setClienteCedula(rs.getString("cliente_cedula"));
                f.setClienteDireccion(rs.getString("cliente_direccion"));
                f.setClienteEmail(rs.getString("cliente_email"));
                f.setClienteTelefono(rs.getString("cliente_telefono"));
                f.setEstablecimiento(rs.getString("establecimiento"));
                f.setPuntoEmision(rs.getString("punto_emision"));
                f.setSecuencial(rs.getString("secuencial"));
                f.setClaveAcceso(rs.getString("clave_acceso"));
                f.setTipoEmision(rs.getString("tipo_emision"));
                f.setTotal(rs.getBigDecimal("total"));
                lista.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Factura obtenerPorId(int id) {
        String sql = "SELECT * FROM factura WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Factura f = new Factura();
                    f.setId(rs.getInt("id"));
                    f.setFecha(rs.getTimestamp("fecha"));
                    f.setClienteNombre(rs.getString("cliente_nombre"));
                    f.setClienteCedula(rs.getString("cliente_cedula"));
                    f.setClienteDireccion(rs.getString("cliente_direccion"));
                    f.setClienteEmail(rs.getString("cliente_email"));
                    f.setClienteTelefono(rs.getString("cliente_telefono"));
                    f.setEstablecimiento(rs.getString("establecimiento"));
                    f.setPuntoEmision(rs.getString("punto_emision"));
                    f.setSecuencial(rs.getString("secuencial"));
                    f.setClaveAcceso(rs.getString("clave_acceso"));
                    f.setTipoEmision(rs.getString("tipo_emision"));
                    f.setTotal(rs.getBigDecimal("total"));
                    return f;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insertar(Factura factura) {
        String sql = "INSERT INTO factura (fecha, cliente_nombre, cliente_cedula, cliente_direccion, cliente_email, cliente_Telefono, establecimiento, punto_emision, secuencial, clave_acceso, tipo_emision, total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ResultSet rs = null;
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, new Timestamp(factura.getFecha().getTime()));
            ps.setString(2, factura.getClienteNombre());
            ps.setString(3, factura.getClienteCedula());
            ps.setString(4, factura.getClienteDireccion());
            ps.setString(5, factura.getClienteEmail());
            ps.setString(6, factura.getClienteTelefono());
            ps.setString(7, factura.getEstablecimiento());
            ps.setString(8, factura.getPuntoEmision());
            ps.setString(9, factura.getSecuencial());
            ps.setString(10, factura.getClaveAcceso());
            ps.setString(11, factura.getTipoEmision());
            ps.setBigDecimal(12, factura.getTotal());

            int filas = ps.executeUpdate();
            if (filas == 0) return -1;

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // id generado
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return -1;
    }

    @Override
    public String obtenerSiguienteSecuencial(String establecimiento, String puntoEmision) {
        String sql = "SELECT MAX(secuencial) AS max_secuencial FROM factura WHERE establecimiento = ? AND punto_emision = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, establecimiento);
            ps.setString(2, puntoEmision);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maxSecuencial = rs.getString("max_secuencial");
                    if (maxSecuencial == null) {
                        return "00000001";
                    }
                    int siguiente = Integer.parseInt(maxSecuencial) + 1;
                    return String.format("%08d", siguiente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "00000001";
    }
}