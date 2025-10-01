package com.example.primer_proyecto.repositories;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.primer_proyecto.api.models.ApiService;
import com.example.primer_proyecto.api.models.Cliente;
import com.example.primer_proyecto.api.models.RetrofitClient;
import com.example.primer_proyecto.database.AppDatabase;
import com.example.primer_proyecto.database.dao.ClienteDao;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteRepository {
    private final ClienteDao clienteDao;
    private final ApiService apiService;

    public ClienteRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        clienteDao = db.clienteDao();
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<Cliente>> obtenerTodosClientes() {
        return clienteDao.obtenerTodos();
    }

    public void sincronizarClientes() {
        apiService.getClientes().enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AppDatabase.databaseWriteExecutor.execute(() ->
                            clienteDao.insertAll(response.body())
                    );
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Log.e("ClienteRepository", "Error API: " + t.getMessage());
            }
        });
    }

    public Cliente obtenerClientePorId(int id) {
        return clienteDao.obtenerPorId(id);
    }

}
