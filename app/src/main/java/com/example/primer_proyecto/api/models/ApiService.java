package com.example.primer_proyecto.api.models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("empleados/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("clientes")
    Call<List<Cliente>> getClientes();

    @GET("inventario")
    Call<List<Inventario>> getProductos();

    @POST("pedidos")
    Call<PedidoResponse> crearPedido(@Body PedidoRequest pedidoRequest);
}
