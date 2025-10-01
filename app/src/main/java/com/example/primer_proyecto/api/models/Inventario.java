package com.example.primer_proyecto.api.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventario")
public class Inventario {
    @PrimaryKey
    @NonNull
    private int IdProducto;
    private String NombreProducto;
    private int StockActual;
    private double Precio;

    public Inventario(){}
    public int getIdProducto() {
        return IdProducto;
    }

    public void setIdProducto(int idProducto) {
        IdProducto = idProducto;
    }

    public String getNombreProducto() {
        return NombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        NombreProducto = nombreProducto;
    }

    public int getStockActual() {
        return StockActual;
    }

    public void setStockActual(int stockActual) {
        StockActual = stockActual;
    }

    public double getPrecio() {
        return Precio;
    }

    public void setPrecio(double precio) {
        Precio = precio;
    }
}
