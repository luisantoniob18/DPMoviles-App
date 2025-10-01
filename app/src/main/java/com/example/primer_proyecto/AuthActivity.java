package com.example.primer_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.primer_proyecto.api.models.Empleado;
import com.example.primer_proyecto.api.models.LoginResponse;
import com.example.primer_proyecto.utils.SessionManager;
import com.example.primer_proyecto.viewmodels.LoginViewModel;

public class AuthActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private LoginViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        initViews();
        setupViewModel();
        setupObservers();

        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
        }
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void setupObservers() {
        viewModel.getLoginResult().observe(this, loginResponse -> {
            if (loginResponse != null) {
                handleLoginResult(loginResponse);
            }
        });

        viewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!isLoading);
        });

        viewModel.getDownloadComplete().observe(this, isComplete -> {
            Log.d("NAVIGATION", "downloadComplete observed: " + isComplete);
            if (isComplete != null && isComplete) {
                navigateToMainActivity();
            }
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.login(username, password);
    }

    private void handleLoginResult(LoginResponse loginResult) {
        if (loginResult.isSuccess()) {
            Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
            //navigateToMainActivity();

            Empleado empleado = loginResult.getEmpleado();
            if (empleado != null) {
                sessionManager.guardarSesionEmpleado(
                        empleado.getIdEmpleado(),
                        empleado.getNombres(),
                        empleado.getApellidos(),
                        empleado.getUsuario(),
                        empleado.getRol(),
                        loginResult.getToken()
                );

                Toast.makeText(this, "Bienvenido " + empleado.getNombres(), Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            } else {
                Toast.makeText(this, "Error: " + loginResult.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Error: " + loginResult.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}