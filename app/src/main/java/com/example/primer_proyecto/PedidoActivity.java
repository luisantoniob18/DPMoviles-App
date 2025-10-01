package com.example.primer_proyecto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.primer_proyecto.adapters.ProductoAdapter;
import com.example.primer_proyecto.api.models.ApiService;
import com.example.primer_proyecto.api.models.Cliente;
import com.example.primer_proyecto.api.models.DetallePedidoRequest;
import com.example.primer_proyecto.api.models.Inventario;
import com.example.primer_proyecto.api.models.Pedido;
import com.example.primer_proyecto.api.models.PedidoRequest;
import com.example.primer_proyecto.api.models.PedidoResponse;
import com.example.primer_proyecto.api.models.ProductoPedido;
import com.example.primer_proyecto.api.models.RetrofitClient;
import com.example.primer_proyecto.database.AppDatabase;
import com.example.primer_proyecto.repositories.ProductoRepository;
import com.example.primer_proyecto.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class PedidoActivity extends AppCompatActivity {
    private Pedido pedido;
    private static final int LOCATION_PERMISSION_CODE = 1001;
    private TextView tvCliente, tvUbicacion;
    private EditText etObservaciones;
    private Button btnObtenerUbicacion, btnGuardarPedido, btnEnviarPedido;
    private ProgressBar progressBar;

    private Cliente clienteSeleccionado;
    private double latitud = 0;
    private double longitud = 0;

    private FusedLocationProviderClient fusedLocationClient;
    private List<ProductoPedido> productosSeleccionados = new ArrayList<>();

    private RecyclerView rvProductosDisponibles;
    private ProductoAdapter productoAdapter;
    private List<Inventario> productosDisponibles = new ArrayList<>();
    private ProductoRepository productoRepository;
    private SessionManager sessionManager;
    private EditText etBuscarProductos;
    private List<Inventario> todosLosProductos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pedido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        productoRepository = new ProductoRepository(this);
        pedido = new Pedido(0, "", 0, 0, "", new ArrayList<>(), null);

        initViews();
        setupLocationClient();
        loadDataFromIntent();
        setupProductosRecyclerView();
        setupBuscador();
        cargarProductos();


    }

    private void initViews() {
        tvCliente = findViewById(R.id.etCliente);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnObtenerUbicacion = findViewById(R.id.btnObtenerUbicacion);
        btnGuardarPedido = findViewById(R.id.btnGuardarPedido);
        btnEnviarPedido = findViewById(R.id.btnEnviarPedido);
        progressBar = findViewById(R.id.progressBar);

        btnObtenerUbicacion.setOnClickListener(v -> requestLocation());
        btnGuardarPedido.setOnClickListener(v -> guardarPedido());
        btnEnviarPedido.setOnClickListener(v -> enviarPedido());

        findViewById(R.id.toolbar).setOnClickListener(v -> onBackPressed());

        rvProductosDisponibles = findViewById(R.id.rvProductosDisponibles);
        etBuscarProductos = findViewById(R.id.etBuscarProductos);
    }

    private void setupBuscador() {
        etBuscarProductos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etBuscarProductos.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etBuscarProductos.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void filtrarProductos(String textoBusqueda) {
        if (todosLosProductos.isEmpty()) return;

        List<Inventario> productosFiltrados;
        if (textoBusqueda.isEmpty()) {
            productosFiltrados = new ArrayList<>(todosLosProductos);
        } else {
            productosFiltrados = new ArrayList<>();
            for (Inventario producto : todosLosProductos) {
                if (producto.getNombreProducto().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                    productosFiltrados.add(producto);
                }
            }


        }

        productosDisponibles.clear();
        productosDisponibles.addAll(productosFiltrados);
        productoAdapter.notifyDataSetChanged();

        /*if (productosFiltrados.isEmpty() && !textoBusqueda.isEmpty()) {
            Toast.makeText(this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
        }*/
        }

    private void setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            int clienteId = intent.getIntExtra("cliente_id", -1);
            String clienteNombre = intent.getStringExtra("cliente_nombre");

            if (clienteId != -1) {
                clienteSeleccionado = new Cliente(clienteId, clienteNombre, "", "", "");
                tvCliente.setText(clienteNombre);
            }
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        } else {
            obtenerUbicacion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnObtenerUbicacion.setEnabled(false);

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        progressBar.setVisibility(View.GONE);
                        btnObtenerUbicacion.setEnabled(true);

                        if (location != null) {
                            latitud = location.getLatitude();
                            longitud = location.getLongitude();

                            tvUbicacion.setText(String.format("Lat: %.6f, Long: %.6f", latitud, longitud));
                            Toast.makeText(PedidoActivity.this, "Ubicación obtenida", Toast.LENGTH_SHORT).show();
                        } else {
                            obtenerUbicacionFallback();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        btnObtenerUbicacion.setEnabled(true);
                        obtenerUbicacionFallback();
                    }
                });
    }

    private void obtenerUbicacionFallback() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Se necesitan permisos de ubicación", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitud = location.getLatitude();
                            longitud = location.getLongitude();
                            tvUbicacion.setText(String.format("Lat: %.6f, Long: %.6f", latitud, longitud));
                            Toast.makeText(PedidoActivity.this, "Ubicación obtenida (caché)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PedidoActivity.this, "Active el GPS y vuelva a intentar", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void guardarPedido() {
        if (clienteSeleccionado == null) {
            Toast.makeText(this, "Seleccione un cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (latitud == 0 || longitud == 0) {
            Toast.makeText(this, "Obtenga la ubicación primero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Agregue productos al pedido", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer idEmpleado = null;
        if (sessionManager.isLoggedIn()) {
            int id = sessionManager.getIdEmpleado();
            if (id != -1) {
                idEmpleado = id;
            }
        }

        String observaciones = etObservaciones.getText().toString().trim();

         /*this.pedido = new Pedido(
                clienteSeleccionado.getIdCliente(),
                clienteSeleccionado.getNombre(),
                latitud,
                longitud,
                observaciones,
                 new ArrayList<>(productosSeleccionados),
                 idEmpleado
        );*/

        if (this.pedido == null) {
            this.pedido = new Pedido();
        }

        this.pedido.setIdCliente(clienteSeleccionado.getIdCliente());
        this.pedido.setNombreCliente(clienteSeleccionado.getNombre());
        this.pedido.setLatitud(latitud);
        this.pedido.setLongitud(longitud);
        this.pedido.setObservaciones(observaciones);
        this.pedido.setProductos(new ArrayList<>(productosSeleccionados));

        idEmpleado = sessionManager.isLoggedIn() ? sessionManager.getIdEmpleado() : null;
        this.pedido.setIdEmpleado(idEmpleado);

        guardarPedidoEnDB(pedido);
    }

    private void guardarPedidoEnDB(Pedido pedido) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("PedidoDebug", "💾 Guardando pedido en BD...");

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(PedidoActivity.this);

                pedido.setEstado("local");

                // ✅ CAPTURAR EL ID GENERADO
                long idGenerado = db.pedidoDao().insert(pedido);
                Log.d("PedidoDebug", "✅ Pedido insertado con ID generado: " + idGenerado);

                // ✅ ACTUALIZAR EL PEDIDO CON EL ID REAL
                pedido.setId((int) idGenerado);

                // ✅ OBTENER EL PEDIDO COMPLETO DE LA BD PARA VERIFICAR
                Pedido pedidoCompleto = db.pedidoDao().obtenerPorId((int) idGenerado);
                Log.d("PedidoDebug", "🔍 Pedido verificado en BD - ID: " + pedidoCompleto.getId() + ", Estado: " + pedidoCompleto.getEstado());

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PedidoActivity.this, "Pedido guardado. ID: " + pedido.getId(), Toast.LENGTH_SHORT).show();
                    btnEnviarPedido.setEnabled(true);

                    // ✅ VERIFICACIÓN FINAL
                    Log.d("PedidoDebug", "📋 Variable pedido actualizada - ID: " + PedidoActivity.this.pedido.getId());
                });

            } catch (Exception e) {
                Log.e("PedidoDebug", "❌ Error guardando pedido", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PedidoActivity.this, "Error al guardar pedido: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void enviarPedido() {
        Log.d("PedidoDebug", "Enviar pedido - pedido es null: " + (pedido == null));

        if (pedido == null) {
            Toast.makeText(this, "No hay pedido para enviar", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Enviando pedido a API...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                ApiService apiService = RetrofitClient.getApiService();
                PedidoRequest pedidoRequest = convertirPedidoARequest(pedido);

                if (pedidoRequest == null) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error al crear la solicitud", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                Response<PedidoResponse> response = apiService.crearPedido(pedidoRequest).execute();

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("PedidoDebug", "✅ Respuesta exitosa de la API");

                        // ✅ ACTUALIZAR STOCK LOCAL después del envío exitoso
                        actualizarStockLocal(pedido.getProductos(), new Runnable() {
                            @Override
                            public void run() {
                                // Luego actualizar el estado del pedido
                                actualizarEstadoPedidoLocal(pedido.getId(), "enviado", new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PedidoActivity.this, "Pedido enviado exitosamente", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });

                    } else {
                        Log.d("PedidoDebug", "❌ Error del servidor: " + response.code());

                        actualizarEstadoPedidoLocal(pedido.getId(), "error", new Runnable() {
                            @Override
                            public void run() {
                                String errorMessage = "Error: " + response.code();
                                if (response.errorBody() != null) {
                                    try {
                                        errorMessage = response.errorBody().string();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Toast.makeText(PedidoActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    actualizarEstadoPedidoLocal(pedido.getId(), "error", new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PedidoActivity.this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                });
            }
        }).start();
    }

    // ✅ NUEVO MÉTODO PARA ACTUALIZAR STOCK LOCAL
    private void actualizarStockLocal(List<ProductoPedido> productosVendidos, Runnable onComplete) {
        Log.d("PedidoDebug", "🔄 Actualizando stock local...");

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);

                for (ProductoPedido productoVendido : productosVendidos) {
                    // Obtener el producto actual de la BD
                    Inventario producto = db.inventarioDao().obtenerPorId(productoVendido.getIdProducto());

                    if (producto != null) {
                        // Calcular nuevo stock
                        int nuevoStock = producto.getStockActual() - productoVendido.getCantidad();
                        if (nuevoStock < 0) nuevoStock = 0;

                        // Actualizar en la BD
                        db.inventarioDao().actualizarStock(productoVendido.getIdProducto(), nuevoStock);

                        Log.d("PedidoDebug", "📦 Stock actualizado - Producto: " + producto.getNombreProducto() +
                                ", Stock anterior: " + producto.getStockActual() +
                                ", Cantidad vendida: " + productoVendido.getCantidad() +
                                ", Nuevo stock: " + nuevoStock);
                    }
                }

                Log.d("PedidoDebug", "✅ Stock local actualizado correctamente");

                // Ejecutar callback cuando termine
                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }

            } catch (Exception e) {
                Log.e("PedidoDebug", "❌ Error actualizando stock local", e);

                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }
            }
        }).start();
    }

    private void actualizarEstadoPedidoLocal(int pedidoId, String nuevoEstado, Runnable onComplete) {
        Log.d("PedidoDebug", "🔄 Actualizando pedido " + pedidoId + " a estado: " + nuevoEstado);

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);
                Pedido pedido = db.pedidoDao().obtenerPorId(pedidoId);

                if (pedido != null) {
                    Log.d("PedidoDebug", "📋 Pedido encontrado. Estado anterior: " + pedido.getEstado());

                    pedido.setEstado(nuevoEstado);
                    db.pedidoDao().update(pedido);

                    Log.d("PedidoDebug", "✅ Pedido " + pedidoId + " actualizado a: " + nuevoEstado);

                    // ✅ VERIFICAR QUE REALMENTE SE ACTUALIZÓ
                    Pedido pedidoVerificado = db.pedidoDao().obtenerPorId(pedidoId);
                    Log.d("PedidoDebug", "🔍 Verificación - Estado actual: " + pedidoVerificado.getEstado());

                } else {
                    Log.e("PedidoDebug", "❌ Pedido " + pedidoId + " no encontrado en BD");
                }

                // ✅ EJECUTAR CALLBACK CUANDO TERMINE
                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }

            } catch (Exception e) {
                Log.e("PedidoDebug", "❌ Error actualizando pedido " + pedidoId, e);

                // ✅ EJECUTAR CALLBACK AUNQUE HAYA ERROR
                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }
            }
        }).start();
    }

    private PedidoRequest convertirPedidoARequest(Pedido pedido) {
        if (pedido == null) {
            Log.e("PedidoDebug", "Pedido es null");
            return null;
        }

        if (pedido.getProductos() == null) {
            Log.e("PedidoDebug", "Productos es null");
            return null;
        }

        if (pedido.getProductos().isEmpty()) {
            Log.e("PedidoDebug", "Productos está vacío");
            return null;
        }

        try {
            List<DetallePedidoRequest> detalles = new ArrayList<>();

            if (pedido.getProductos() != null && !pedido.getProductos().isEmpty()) {
                for (ProductoPedido producto : pedido.getProductos()) {
                    detalles.add(new DetallePedidoRequest(
                            producto.getIdProducto(),
                            producto.getCantidad()
                    ));

                    Log.d("PedidoDebug", "Producto: ID=" + producto.getIdProducto() +
                            ", Cantidad=" + producto.getCantidad());
                }
            }

            Integer idEmpleado = null;
            if (sessionManager.isLoggedIn()) {
                int id = sessionManager.getIdEmpleado();
                if (id != -1) {
                    idEmpleado = id;
                }
            }

            PedidoRequest request = new PedidoRequest(
                    pedido.getIdCliente(),
                    detalles,
                    pedido.getLatitud(),
                    pedido.getLongitud(),
                    idEmpleado
            );
            Log.d("PedidoDebug", "Request: IdCliente=" + pedido.getIdCliente() +
                    ", Productos=" + detalles.size() +
                    ", Lat=" + pedido.getLatitud() + ", Long=" + pedido.getLongitud());

            return request;

        } catch (Exception e) {
            Log.e("PedidoDebug", "Error converting pedido to request", e);
            return null;
        }
    }

    private void setupProductosRecyclerView() {
        productoAdapter = new ProductoAdapter(productosDisponibles, new ProductoAdapter.OnProductoClickListener() {
            @Override
            public void onProductoClick(Inventario producto) {
                mostrarDialogoCantidad(producto);
            }
        });

        rvProductosDisponibles.setLayoutManager(new LinearLayoutManager(this));
        rvProductosDisponibles.setAdapter(productoAdapter);

        // ✅ EVITAR QUE EL RECYCLERVIEW TOME FOCO
        rvProductosDisponibles.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        rvProductosDisponibles.setFocusable(false);
        rvProductosDisponibles.setFocusableInTouchMode(false);
    }

    private void cargarProductos() {
        new Thread(() -> {
            List<Inventario> productos = productoRepository.obtenerTodosProductos();

            runOnUiThread(() -> {
                todosLosProductos.clear();
                todosLosProductos.addAll(productos);

                productosDisponibles.clear();
                productosDisponibles.addAll(productos);
                productoAdapter.notifyDataSetChanged();

                if (productos.isEmpty()) {
                    Toast.makeText(this, "No hay productos disponibles", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void mostrarDialogoCantidad(Inventario producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar cantidad para: " + producto.getNombreProducto());

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Cantidad");
        builder.setView(input);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String cantidadStr = input.getText().toString();
            if (!cantidadStr.isEmpty()) {
                int cantidad = Integer.parseInt(cantidadStr);
                if (cantidad > 0) {
                    agregarProductoAlPedido(producto, cantidad);
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void agregarProductoAlPedido(Inventario producto, int cantidad) {
        ProductoPedido productoPedido = new ProductoPedido(
                producto.getIdProducto(),
                producto.getNombreProducto(),
                producto.getPrecio(),
                cantidad
        );

        if (this.pedido != null) {
            this.pedido.getProductos().add(productoPedido);
        }

        productosSeleccionados.add(productoPedido);
        actualizarResumenProductos();
        Toast.makeText(this, "Producto agregado al pedido", Toast.LENGTH_SHORT).show();
    }

    private void actualizarResumenProductos() {
        TextView tvResumenProductos = findViewById(R.id.tvProductosSeleccionados);
        if (tvResumenProductos != null) {
            StringBuilder resumen = new StringBuilder("Productos seleccionados:\n");
            for (ProductoPedido pp : productosSeleccionados) {
                resumen.append(pp.getCantidad())
                        .append("x ")
                        .append(pp.getNombre())
                        .append(" - $")
                        .append(pp.getPrecio() * pp.getCantidad())
                        .append("\n");
            }
            tvResumenProductos.setText(resumen.toString());
        }
    }
}
