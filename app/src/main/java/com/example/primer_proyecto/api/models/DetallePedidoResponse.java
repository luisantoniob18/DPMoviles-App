package com.example.primer_proyecto.api.models;

import com.google.gson.annotations.SerializedName;

public class DetallePedidoResponse {
    @SerializedName("IdDetallePedido")
    private int idDetallePedido;

    @SerializedName("IdPedido")
    private int idPedido;

    @SerializedName("IdProducto")
    private int idProducto;

    @SerializedName("Cantidad")
    private int cantidad;

    @SerializedName("Subtotal")
    private double subtotal;

    public int getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(int idDetallePedido) {
        this.idDetallePedido = idDetallePedido;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
