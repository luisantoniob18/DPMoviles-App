package com.example.primer_proyecto.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PedidoResponse {
    @SerializedName("IdPedido")
    private int idPedido;

    @SerializedName("IdCliente")
    private int idCliente;

    @SerializedName("Total")
    private double total;

    @SerializedName("Estado")
    private int estado;

    @SerializedName("Latitud")
    private double latitud;

    @SerializedName("Longitud")
    private double longitud;

    @SerializedName("Fecha")
    private String fecha;

    @SerializedName("IdEmpleado")
    private Integer idEmpleado;

    @SerializedName("detalles")
    private List<DetallePedidoResponse> detalles;

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public List<DetallePedidoResponse> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoResponse> detalles) {
        this.detalles = detalles;
    }
}
