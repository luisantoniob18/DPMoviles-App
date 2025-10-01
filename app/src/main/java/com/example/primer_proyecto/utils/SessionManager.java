package com.example.primer_proyecto.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.primer_proyecto.api.models.RetrofitClient;

public class SessionManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_ID_EMPLEADO = "empleado_id";
    private static final String KEY_NOMBRE_EMPLEADO = "empleado_nombres";
    private static final String KEY_APELLIDO_EMPLEADO = "empleado_apellidos";
    private static final String KEY_USUARIO_EMPLEADO = "empleado_usuario";
    private static final String KEY_ROL_EMPLEADO = "empleado_rol";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void guardarSesionEmpleado(int idEmpleado, String nombres, String apellidos,
                                      String usuario, String rol, String authToken) {
        editor.putInt(KEY_ID_EMPLEADO, idEmpleado);
        editor.putString(KEY_NOMBRE_EMPLEADO, nombres);
        editor.putString(KEY_APELLIDO_EMPLEADO, apellidos);
        editor.putString(KEY_USUARIO_EMPLEADO, usuario);
        editor.putString(KEY_ROL_EMPLEADO, rol);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();

        RetrofitClient.setAuthToken(authToken);
    }

    public int getIdEmpleado() {
        return sharedPreferences.getInt(KEY_ID_EMPLEADO, -1);
    }

    public String getNombreCompletoEmpleado() {
        String nombres = sharedPreferences.getString(KEY_NOMBRE_EMPLEADO, "");
        String apellidos = sharedPreferences.getString(KEY_APELLIDO_EMPLEADO, "");
        return nombres + " " + apellidos;
    }

    public String getUsuarioEmpleado() {
        return sharedPreferences.getString(KEY_USUARIO_EMPLEADO, "");
    }

    public String getRolEmpleado() {
        return sharedPreferences.getString(KEY_ROL_EMPLEADO, "");
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, "");
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void cerrarSesion() {
        editor.clear();
        editor.apply();
        RetrofitClient.setAuthToken("");
    }

}
