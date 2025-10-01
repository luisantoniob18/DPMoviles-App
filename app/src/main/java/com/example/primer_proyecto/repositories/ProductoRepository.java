package com.example.primer_proyecto.repositories;

import android.content.Context;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.example.primer_proyecto.api.models.ApiService;
import com.example.primer_proyecto.api.models.Inventario;
import com.example.primer_proyecto.api.models.RetrofitClient;
import com.example.primer_proyecto.database.AppDatabase;
import com.example.primer_proyecto.database.dao.InventarioDao;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoRepository {
    private final InventarioDao inventarioDao;
    private final ApiService apiService;
    private final Executor backgroundExecutor;
    private final Executor mainExecutor;
    public ProductoRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        inventarioDao = database.inventarioDao();
        apiService = RetrofitClient.getApiService();
        this.backgroundExecutor = Executors.newSingleThreadExecutor();
        this.mainExecutor = ContextCompat.getMainExecutor(context);
    }

    public List<Inventario> obtenerTodosProductos() {
        return inventarioDao.obtenerTodos();
    }

    public List<Inventario> obtenerProductosConStock() {
        return inventarioDao.obtenerConStock();
    }

    public List<Inventario> obtenerProductosSinStock() {
        return inventarioDao.obtenerSinStock();
    }

    public void actualizarStock(int id, int nuevoStock) {
        inventarioDao.actualizarStock(id, nuevoStock);
    }

    public void guardarProductos(List<Inventario> productos) {
        inventarioDao.insertAll(productos);
    }

    public void sincronizarProductos(String token, SincronizacionCallback callback) {
        apiService.getProductos().enqueue(new Callback<List<Inventario>>() {
            @Override
            public void onResponse(Call<List<Inventario>> call, Response<List<Inventario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Inventario> productos = response.body();

                    backgroundExecutor.execute(() -> {
                        inventarioDao.insertAll(productos);

                        mainExecutor.execute(() -> callback.onExito(productos.size()));
                    });
                } else {
                    callback.onError("Error API: Código " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Inventario>> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public interface SincronizacionCallback {
        void onExito(int cantidadProductos);
        void onError(String mensajeError);
    }
}
