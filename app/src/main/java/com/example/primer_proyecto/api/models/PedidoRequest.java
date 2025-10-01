package com.example.primer_proyecto.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PedidoRequest {
    private int IdCliente;

    private List<DetallePedidoRequest> detalles;

    private double Latitud;

    private double Longitud;

    private Integer IdEmpleado;

    public PedidoRequest(int IdCliente, List<DetallePedidoRequest> detalles,
                         double Latitud, double Longitud, Integer IdEmpleado) {
        this.IdCliente = IdCliente;
        this.detalles = detalles;
        this.Latitud = Latitud;
        this.Longitud = Longitud;
        this.IdEmpleado = IdEmpleado;
    }

    public int getIdCliente() {
        return IdCliente;
    }

    public void setIdCliente(int idCliente) {
        IdCliente = idCliente;
    }

    public List<DetallePedidoRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoRequest> detalles) {
        this.detalles = detalles;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }

    public Integer getIdEmpleado() {
        return IdEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        IdEmpleado = idEmpleado;
    }
}
