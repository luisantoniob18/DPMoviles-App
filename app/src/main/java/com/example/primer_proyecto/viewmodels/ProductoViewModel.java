package com.example.primer_proyecto.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.primer_proyecto.api.models.Inventario;
import com.example.primer_proyecto.repositories.ProductoRepository;

import java.util.List;

public class ProductoViewModel extends AndroidViewModel {
    private final ProductoRepository productoRepository;
    private final MutableLiveData<List<Inventario>> productos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ProductoViewModel(Application application) {
        super(application);
        productoRepository = new ProductoRepository(application);
    }

    public LiveData<List<Inventario>> getProductos() {
        return productos;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cargarProductos() {
        loading.setValue(true);

        new Thread(() -> {
            try {
                List<Inventario> productosDB = productoRepository.obtenerTodosProductos();
                productos.postValue(productosDB);
                error.postValue(null);
            } catch (Exception e) {
                error.postValue("Error al cargar productos: " + e.getMessage());
            } finally {
                loading.postValue(false);
            }
        }).start();
    }

}
