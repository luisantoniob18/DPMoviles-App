package com.example.primer_proyecto.api.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.primer_proyecto.database.converters.DateConverter;
import com.example.primer_proyecto.database.converters.ProductoPedidoConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "pedidos")
public class Pedido {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int idCliente;
    private String nombreCliente;
    private double latitud;
    private double longitud;
    private String observaciones;
    private String estado;

    private Integer IdEmpleado;

    @TypeConverters(DateConverter.class)
    private Date fechaCreacion;

    @TypeConverters({ProductoPedidoConverter.class})
    private List<ProductoPedido> productos;

    public Pedido() {
        this.fechaCreacion = new Date();
        this.estado = "local";
        this.productos = new ArrayList<>();
    }

    public Pedido(int idCliente, String nombreCliente, double latitud, double longitud,
                  String observaciones, List<ProductoPedido> productos, Integer idEmpleado) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.latitud = latitud;
        this.longitud = longitud;
        this.observaciones = observaciones;
        this.productos = productos;
        this.fechaCreacion = new Date();
        this.estado = "local";
        this.IdEmpleado = idEmpleado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ProductoPedido> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoPedido> productos) {
        this.productos = productos;
    }

    public int getIdEmpleado() {
        return IdEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        IdEmpleado = idEmpleado;
    }
}
