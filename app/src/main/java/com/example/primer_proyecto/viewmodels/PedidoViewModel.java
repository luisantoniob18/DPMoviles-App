package com.example.primer_proyecto.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.primer_proyecto.api.models.Pedido;
import com.example.primer_proyecto.repositories.PedidoRepository;

import java.util.List;

public class PedidoViewModel extends AndroidViewModel {
    private final PedidoRepository pedidoRepository;
    private final MutableLiveData<List<Pedido>> pedidos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> sincronizacion = new MutableLiveData<>();

    public PedidoViewModel(Application application) {
        super(application);
        pedidoRepository = new PedidoRepository(application);
    }

    public LiveData<List<Pedido>> getPedidos() {
        return pedidos;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSincronizacion() {
        return sincronizacion;
    }

    public void cargarPedidos() {
        loading.setValue(true);

        new Thread(() -> {
            try {
                List<Pedido> pedidosDB = pedidoRepository.obtenerTodos();
                pedidos.postValue(pedidosDB);
                error.postValue(null);
            } catch (Exception e) {
                error.postValue("Error al cargar pedidos: " + e.getMessage());
            } finally {
                loading.postValue(false);
            }
        }).start();
    }

    public void sincronizarPedidosPendientes() {
        loading.setValue(true);

        new Thread(() -> {
            try {
                int pedidosSincronizados = pedidoRepository.sincronizarPendientes();
                sincronizacion.postValue(pedidosSincronizados + " pedidos sincronizados");
            } catch (Exception e) {
                error.postValue("Error en sincronización: " + e.getMessage());
            } finally {
                loading.postValue(false);
            }
        }).start();
    }

    public void reenviarPedido(int pedidoId) {
        new Thread(() -> {
            try {
                boolean exito = pedidoRepository.reenviarPedido(pedidoId);
                if (exito) {
                    sincronizacion.postValue("Pedido reenviado exitosamente");
                    cargarPedidos();
                } else {
                    error.postValue("Error al reenviar pedido");
                }
            } catch (Exception e) {
                error.postValue("Error: " + e.getMessage());
            }
        }).start();
    }

}
