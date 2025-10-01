package com.example.primer_proyecto.api.models;

public class LoginRequest {
    private String Usuario;
    private String Password;

    public LoginRequest(String usuario, String password) {
        Usuario = usuario;
        Password = password;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
