package com.example.primer_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.primer_proyecto.adapters.ClienteAdapter;
import com.example.primer_proyecto.api.models.Cliente;
import com.example.primer_proyecto.viewmodels.ClienteViewModel;

import java.util.List;

public class ClientesActivity extends AppCompatActivity implements ClienteAdapter.OnClienteClickListener {
    private RecyclerView rvClientes;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ClienteViewModel viewModel;
    private ClienteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clientes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        initViews();
        setupViewModel();
        setupObservers();
    }

    private void initViews() {
        rvClientes = findViewById(R.id.rvClientes);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvClientes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClienteAdapter(null, this);
        rvClientes.setAdapter(adapter);

        findViewById(R.id.toolbar).setOnClickListener(v -> onBackPressed());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
    }

    private void setupObservers() {
        viewModel.getClientes().observe(this, this::handleClientes);
        viewModel.getLoading().observe(this, this::handleLoading);
        viewModel.getError().observe(this, this::handleError);
    }

    private void handleClientes(List<Cliente> clientes) {
        if (clientes != null && !clientes.isEmpty()) {
            adapter.setClientes(clientes);
            rvClientes.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            rvClientes.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void handleLoading(Boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void handleError(String error) {
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClienteClick(Cliente cliente) {
        Intent intent = new Intent(this, PedidoActivity.class);
        intent.putExtra("cliente_id", cliente.getIdCliente());
        intent.putExtra("cliente_nombre", cliente.getNombre());
        startActivity(intent);
    }
}