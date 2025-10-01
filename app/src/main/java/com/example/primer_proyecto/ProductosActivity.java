package com.example.primer_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

import com.example.primer_proyecto.adapters.ProductoAdapter;
import com.example.primer_proyecto.api.models.Inventario;
import com.example.primer_proyecto.viewmodels.ProductoViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class ProductosActivity extends AppCompatActivity implements ProductoAdapter.OnProductoClickListener {
    private RecyclerView rvProductos;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private Chip chipTodos, chipConStock, chipSinStock;

    private ProductoViewModel viewModel;
    private ProductoAdapter adapter;
    private List<Inventario> todosLosProductos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModel();
        setupObservers();
        setupSearchView();
        setupChips();
        setupBackPressedHandler();
        loadProductos();
    }

    private void initViews() {
        rvProductos = findViewById(R.id.rvProductos);
        searchView = findViewById(R.id.searchView);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        chipTodos = findViewById(R.id.chipTodos);
        chipConStock = findViewById(R.id.chipConStock);
        chipSinStock = findViewById(R.id.chipSinStock);

        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(new ArrayList<>(), this);
        rvProductos.setAdapter(adapter);

        findViewById(R.id.toolbar).setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductos();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
    }

    private void setupObservers() {
        viewModel.getProductos().observe(this, this::handleProductos);
        viewModel.getLoading().observe(this, this::handleLoading);
        viewModel.getError().observe(this, this::handleError);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProductos(newText);
                return true;
            }
        });
    }

    private void setupChips() {
        chipTodos.setChecked(true);

        chipTodos.setOnClickListener(v -> filterByStock("todos"));
        chipConStock.setOnClickListener(v -> filterByStock("con_stock"));
        chipSinStock.setOnClickListener(v -> filterByStock("sin_stock"));
    }

    private void loadProductos() {
        viewModel.cargarProductos();
    }

    private void handleProductos(List<Inventario> productos) {
        if (productos != null && !productos.isEmpty()) {
            todosLosProductos = productos;
            adapter.setProductos(productos);
            rvProductos.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            rvProductos.setVisibility(View.GONE);
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

    private void filterProductos(String query) {
        if (todosLosProductos.isEmpty()) return;

        List<Inventario> filtered = new ArrayList<>();
        for (Inventario producto : todosLosProductos) {
            /*if (producto.getNombreProducto().toLowerCase().contains(query.toLowerCase()) ||
                    (producto.getDescripcion() != null &&
                            producto.getDescripcion().toLowerCase().contains(query.toLowerCase()))) {
                filtered.add(producto);
            }*/
        }
        adapter.filterList(filtered);
    }

    private void filterByStock(String filter) {
        if (todosLosProductos.isEmpty()) return;

        List<Inventario> filtered = new ArrayList<>();
        for (Inventario producto : todosLosProductos) {
            switch (filter) {
                case "con_stock":
                    if (producto.getStockActual() > 0) filtered.add(producto);
                    break;
                case "sin_stock":
                    if (producto.getStockActual() <= 0) filtered.add(producto);
                    break;
                default:
                    filtered.add(producto);
                    break;
            }
        }
        adapter.filterList(filtered);
    }

    @Override
    public void onProductoClick(Inventario producto) {
        Toast.makeText(this, "Seleccionado: " + producto.getNombreProducto(), Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("producto_id", producto.getIdProducto());
        resultIntent.putExtra("producto_nombre", producto.getNombreProducto());
        resultIntent.putExtra("producto_precio", producto.getPrecio());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }

    });
}
}
