package com.example.primer_proyecto.api.models;

import com.google.gson.annotations.SerializedName;

public class DetallePedidoRequest {
    @SerializedName("IdProducto")
    private int idProducto;

    @SerializedName("Cantidad")
    private int cantidad;

    public DetallePedidoRequest(int idProducto, int cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
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
}
