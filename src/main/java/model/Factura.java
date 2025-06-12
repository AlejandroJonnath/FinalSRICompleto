package model;

import java.math.BigDecimal;
import java.util.Date;

public class Factura {

    private int id;
    private Date fecha;
    private String clienteNombre;
    private String clienteCedula;
    private String clienteDireccion;
    private String clienteEmail;
    private String clienteTelefono;
    private String establecimiento;
    private String puntoEmision;
    private String secuencial;
    private String claveAcceso;
    private String tipoEmision;
    private BigDecimal total;

    public Factura() {

    }

    public Factura(int id, Date fecha, String clienteNombre, String clienteCedula, String clienteDireccion, String clienteEmail, String clienteTelefono, String establecimiento, String puntoEmision, String secuencial, String claveAcceso, String tipoEmision, BigDecimal total) {
        this.id = id;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.clienteCedula = clienteCedula;
        this.clienteDireccion = clienteDireccion;
        this.clienteEmail = clienteEmail;
        this.clienteTelefono = clienteTelefono;
        this.establecimiento = establecimiento;
        this.puntoEmision = puntoEmision;
        this.secuencial = secuencial;
        this.claveAcceso = claveAcceso;
        this.tipoEmision = tipoEmision;
        this.total = total;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteCedula() {
        return clienteCedula;
    }

    public void setClienteCedula(String clienteCedula) {
        this.clienteCedula = clienteCedula;
    }

    public String getClienteDireccion() {
        return clienteDireccion;
    }

    public void setClienteDireccion(String clienteDireccion) {
        this.clienteDireccion = clienteDireccion;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public String getClienteTelefono() {return clienteTelefono; }

    public void setClienteTelefono(String clienteTelefono) {this.clienteTelefono = clienteTelefono; }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getPuntoEmision() {
        return puntoEmision;
    }

    public void setPuntoEmision(String puntoEmision) {
        this.puntoEmision = puntoEmision;
    }

    public String getSecuencial() {
        return secuencial;
    }

    public void setSecuencial(String secuencial) {
        this.secuencial = secuencial;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public String getTipoEmision() {
        return tipoEmision;
    }

    public void setTipoEmision(String tipoEmision) {
        this.tipoEmision = tipoEmision;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}