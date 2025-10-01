package com.example.primer_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
    }

    public void navigateToClientes(View view) {
        try {
            Intent intent = new Intent(this, ClientesActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir Clientes", Toast.LENGTH_SHORT).show();
        }
    }

    public void navigateToProductos(View view) {
        try {
            Intent intent = new Intent(this, ProductosActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir Productos", Toast.LENGTH_SHORT).show();
        }
    }

    public void navigateToHistorial(View view) {
        try {
            Intent intent = new Intent(this, HistorialPedidosActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir Historial", Toast.LENGTH_SHORT).show();
        }
    }

    public void navigateToPedido(View view) {
        Intent intent = new Intent(this, PedidoActivity.class);
        intent.putExtra("redirigir_a_pedido", true);
        startActivity(intent);
    }
}