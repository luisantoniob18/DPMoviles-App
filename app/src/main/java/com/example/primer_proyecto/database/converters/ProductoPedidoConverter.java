package com.example.primer_proyecto.database.converters;

import androidx.room.TypeConverter;

import com.example.primer_proyecto.api.models.ProductoPedido;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ProductoPedidoConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromProductoPedidoList(List<ProductoPedido> productos) {
        if (productos == null || productos.isEmpty()) {
            return null;
        }
        return gson.toJson(productos);
    }

    @TypeConverter
    public static List<ProductoPedido> toProductoPedidoList(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        try {
            Type listType = new TypeToken<List<ProductoPedido>>() {}.getType();
            return gson.fromJson(data, listType);
        } catch (Exception e) {
            return null;
        }
    }

}
