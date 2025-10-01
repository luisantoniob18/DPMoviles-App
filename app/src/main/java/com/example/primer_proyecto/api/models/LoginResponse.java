package com.example.primer_proyecto.api.models;

public class LoginResponse {
    private String token;
    private Empleado empleado;
    private boolean success;
    private String message;

    //constructor para exito
    public LoginResponse(String token, Empleado empleado) {
        this.token = token;
        this.empleado = empleado;
        this.success = true;
        this.message = "Login exitoso";
    }

    //constructor para error
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.token = "";
        this.empleado = null;
    }

    public String getToken() {
        return token;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
