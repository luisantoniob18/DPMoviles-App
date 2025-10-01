package com.example.primer_proyecto.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.primer_proyecto.api.models.Cliente;
import com.example.primer_proyecto.repositories.ClienteRepository;

import java.util.List;

public class ClienteViewModel extends AndroidViewModel {
    private final ClienteRepository clienteRepository;
    private final LiveData<List<Cliente>> clientes;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ClienteViewModel(Application application) {
        super(application);
        clienteRepository = new ClienteRepository(application);
        clientes = clienteRepository.obtenerTodosClientes();
        clienteRepository.sincronizarClientes();
    }

    public LiveData<List<Cliente>> getClientes() {
        return clientes;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

}
