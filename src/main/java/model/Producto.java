package model;

import java.math.BigDecimal;

public class Producto {

    private int id;
    private String nombre;
    private String marca;
    private BigDecimal precio;
    private int stock;
    private boolean activo;
    private Categoria categoria; // Relación

    // Constructor vacío (por defecto)
    public Producto() {
    }

    // Constructor completo
    public Producto(int id, String nombre, String marca, BigDecimal precio, int stock, boolean activo, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.precio = precio;
        this.stock = stock;
        this.activo = activo;
        this.categoria = categoria;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}