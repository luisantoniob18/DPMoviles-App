package com.example.primer_proyecto.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.primer_proyecto.api.models.Cliente;
import com.example.primer_proyecto.api.models.Empleado;
import com.example.primer_proyecto.api.models.Inventario;
import com.example.primer_proyecto.api.models.LoginRequest;
import com.example.primer_proyecto.api.models.LoginResponse;
import com.example.primer_proyecto.api.models.RetrofitClient;
import com.example.primer_proyecto.repositories.ProductoRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> downloadComplete = new MutableLiveData<>();

    private final Application application;

    private final ProductoRepository productoRepository;
    public LoginViewModel(@NonNull Application application) {
        super(application);
        productoRepository = new ProductoRepository(application);
        this.application = application;
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<Boolean> getDownloadComplete() {
        return downloadComplete;
    }

    public void login(String username, String password) {
        loading.setValue(true);

        LoginRequest loginRequest = new LoginRequest(username, password);

        RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse apiResponse = response.body();

                    String authToken = apiResponse.getToken();

                    if (authToken != null && !authToken.isEmpty()) {
                        guardarToken(authToken);
                        guardarInfoEmpleado(apiResponse.getEmpleado());

                        loginResult.setValue(new LoginResponse(authToken, apiResponse.getEmpleado()));
                        downloadData(authToken);
                    } else {
                        loginResult.setValue(new LoginResponse(false, "Token no recibido"));
                        loading.setValue(false);
                    }
                } else {
                    loginResult.setValue(new LoginResponse(false, "Error en el servidor"));
                    loading.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResult.setValue(new LoginResponse(false, "Error de conexión: " + t.getMessage()));
                loading.setValue(false);
            }
        });
    }


private void guardarToken(String token) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("auth_token", token);
    editor.apply();
}

private void guardarInfoEmpleado(Empleado empleado) {
    if (empleado != null) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("empleado_id", empleado.getIdEmpleado());
        editor.putString("empleado_nombres", empleado.getNombres());
        editor.putString("empleado_apellidos", empleado.getApellidos());
        editor.putString("empleado_usuario", empleado.getUsuario());
        editor.putString("empleado_rol", empleado.getRol());
        editor.apply();
    }
}

private void downloadData(String authToken) {
    String tokenHeader = "Bearer " + authToken;

    RetrofitClient.getApiService().getClientes().enqueue(new Callback<List<Cliente>>() {
        @Override
        public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
            if (response.isSuccessful() && response.body() != null) {
                List<Cliente> clientes = response.body();
                guardarClientesEnDB(clientes);
                descargarProductos(tokenHeader);

                downloadComplete.postValue(true);

                loading.postValue(false);
            } else {
                handleDownloadError("Error descargando clientes: " + response.code());
            }
        }
        @Override
        public void onFailure(Call<List<Cliente>> call, Throwable t) {
            handleDownloadError("Error de conexión clientes: " + t.getMessage());
        }
    });
    }
    private void guardarClientesEnDB(List<Cliente> clientes) {
        if (clientes != null && !clientes.isEmpty()) {
            Log.d("LoginViewModel", "Guardando " + clientes.size() + " clientes en DB");

            for (Cliente cliente : clientes) {
                Log.d("LoginViewModel", "Cliente: " + cliente.getNombre());
            }
        }
    }

    private void descargarProductos(String tokenHeader) {
        productoRepository.sincronizarProductos(tokenHeader, new ProductoRepository.SincronizacionCallback() {
            @Override
            public void onExito(int cantidadProductos) {
                downloadComplete.postValue(true);
                loading.postValue(false);
                Log.d("Download", "Descarga completada: " + cantidadProductos + " productos");
            }

            @Override
            public void onError(String mensajeError) {
                handleDownloadError(mensajeError);
            }
        });
    }

    private void guardarProductosEnDB(List<Inventario> productos) {
        try {
            if (productos != null) {
                Log.d("DB", "Guardando " + productos.size() + " productos");
                for (int i = 0; i < Math.min(productos.size(), 5); i++) {
                    Inventario producto = productos.get(i);
                    Log.d("Producto", (i+1) + ": " + producto.getNombreProducto() +
                            " - $" + producto.getPrecio() +
                            " - Stock: " + producto.getStockActual());
                }
            }
        } catch (Exception e) {
            Log.e("DB", "Error guardando productos: " + e.getMessage());
        }
    }

    private void handleDownloadError(String errorMessage) {
        Log.e("DownloadError", errorMessage);

        loading.postValue(false);

        loginResult.postValue(new LoginResponse(false, errorMessage));
}
}
