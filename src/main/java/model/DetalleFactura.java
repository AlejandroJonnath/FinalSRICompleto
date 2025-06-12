package model;

import java.math.BigDecimal;

public class DetalleFactura {

    private int id;
    private Factura factura;     // Relación a Factura
    private Producto producto;   // Relación a Producto
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private int stock;
    private BigDecimal descuento;
    private BigDecimal iva;        // porcentaje (%), por ejemplo 15.00
    private BigDecimal ivaValor;   // valor en $ → lo que se muestra al cliente

    // Constructor vacío
    public DetalleFactura() {
    }

    // Constructor completo (sin id)
    public DetalleFactura(Factura factura, Producto producto, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal,
                          int stock, BigDecimal descuento, BigDecimal iva, BigDecimal ivaValor) {
        this.factura = factura;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.stock = stock;
        this.descuento = descuento;
        this.iva = iva;
        this.ivaValor = ivaValor;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getIvaValor() {
        return ivaValor;
    }

    public void setIvaValor(BigDecimal ivaValor) {
        this.ivaValor = ivaValor;
    }
}