package com.example.primer_proyecto;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.primer_proyecto.adapters.PedidoAdapter;
import com.example.primer_proyecto.api.models.Pedido;
import com.example.primer_proyecto.viewmodels.PedidoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import com.example.primer_proyecto.api.models.ProductoPedido;

public class HistorialPedidosActivity extends AppCompatActivity implements PedidoAdapter.OnPedidoClickListener {
    private RecyclerView rvPedidos;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabSincronizar;

    private PedidoViewModel viewModel;
    private PedidoAdapter adapter;
    private List<Pedido> todosLosPedidos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial_pedidos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModel();
        setupObservers();
        setupTabs();
        loadPedidos();
    }

    private void initViews() {
        rvPedidos = findViewById(R.id.rvPedidos);
        tabLayout = findViewById(R.id.tabLayout);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabSincronizar = findViewById(R.id.fabSincronizar);

        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PedidoAdapter(new ArrayList<>(), this);
        rvPedidos.setAdapter(adapter);

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        fabSincronizar.setOnClickListener(v -> sincronizarPedidos());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PedidoViewModel.class);
    }

    private void setupObservers() {
        viewModel.getPedidos().observe(this, this::handlePedidos);
        viewModel.getLoading().observe(this, this::handleLoading);
        viewModel.getError().observe(this, this::handleError);
        viewModel.getSincronizacion().observe(this, this::handleSincronizacion);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filtrarPedidos(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadPedidos() {
        viewModel.cargarPedidos();
    }

    private void handlePedidos(List<Pedido> pedidos) {
        if (pedidos != null && !pedidos.isEmpty()) {
            todosLosPedidos = pedidos;
            adapter.setPedidos(pedidos);
            rvPedidos.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            rvPedidos.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void handleLoading(Boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        fabSincronizar.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void handleError(String error) {
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSincronizacion(String mensaje) {
        if (mensaje != null) {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            loadPedidos();
        }
    }

    private void filtrarPedidos(int filtro) {
        Log.d("HistorialDebug", "Filtrando pedidos. Total: " + todosLosPedidos.size() + ", Filtro: " + filtro);

        if (todosLosPedidos.isEmpty()) {
            Log.d("HistorialDebug", "Lista de pedidos vacía");
            tvEmpty.setVisibility(View.VISIBLE);
            rvPedidos.setVisibility(View.GONE);
            return;
        };

        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido pedido : todosLosPedidos) {
            String estado = pedido.getEstado() != null ? pedido.getEstado().toLowerCase() : "sin estado";

            Log.d("HistorialDebug", "Pedido ID: " + pedido.getId() +
                    ", Estado: " + estado +
                    ", Cliente: " + pedido.getNombreCliente());

            switch (filtro) {
                case 0:
                    filtrados.add(pedido);
                    break;
                case 1:
                    if ("local".equalsIgnoreCase(pedido.getEstado()) ||
                            "error".equalsIgnoreCase(pedido.getEstado())) {
                        filtrados.add(pedido);
                    }
                    break;
                case 2:
                    if ("enviado".equalsIgnoreCase(pedido.getEstado())) {
                        filtrados.add(pedido);
                    }
                    break;
            }
        }
        adapter.setPedidos(filtrados);
    }

    private void sincronizarPedidos() {
        viewModel.sincronizarPedidosPendientes();
    }

    @Override
    public void onVerDetallesClick(Pedido pedido) {
        mostrarDetallesPedido(pedido);
    }

    @Override
    public void onReenviarClick(Pedido pedido) {
        viewModel.reenviarPedido(pedido.getId());
    }

    private void mostrarDetallesPedido(Pedido pedido) {
        StringBuilder detalles = new StringBuilder();
        detalles.append("Pedido #").append(pedido.getId()).append("\n");
        detalles.append("Cliente: ").append(pedido.getNombreCliente()).append("\n");
        detalles.append("Fecha: ").append(pedido.getFechaCreacion()).append("\n");
        detalles.append("Ubicación: ").append(pedido.getLatitud()).append(", ").append(pedido.getLongitud()).append("\n");
        detalles.append("Estado: ").append(pedido.getEstado()).append("\n\n");
        detalles.append("Productos:\n");

        if (pedido.getProductos() != null) {
            for (ProductoPedido producto : pedido.getProductos()) {
                detalles.append("- ").append(producto.getNombre())
                        .append(" x").append(producto.getCantidad());
                        //.append(" = $").append(producto.getSubtotal()).append("\n");
            }
        }

        //detalles.append("\nTotal: $").append(pedido.getTotal());

        new android.app.AlertDialog.Builder(this)
                .setTitle("Detalles del Pedido")
                .setMessage(detalles.toString())
                .setPositiveButton("Cerrar", null)
                .show();
    }
}