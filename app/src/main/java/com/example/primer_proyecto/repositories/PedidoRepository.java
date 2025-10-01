package com.example.primer_proyecto.repositories;

import android.content.Context;

import com.example.primer_proyecto.api.models.Pedido;
import com.example.primer_proyecto.database.AppDatabase;
import com.example.primer_proyecto.database.dao.PedidoDao;

import java.util.List;

public class PedidoRepository {
    private final PedidoDao pedidoDao;

    public PedidoRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        pedidoDao = database.pedidoDao();
    }

    public List<Pedido> obtenerTodos() {
        return pedidoDao.obtenerTodos();
    }

    public List<Pedido> obtenerPorEstado(String estado) {
        return pedidoDao.obtenerPorEstado(estado);
    }

    public List<Pedido> obtenerPedidosLocales() {
        return pedidoDao.obtenerPorEstado("local");
    }

    public List<Pedido> obtenerPedidosEnviados() {
        return pedidoDao.obtenerPorEstado("enviado");
    }

    public List<Pedido> obtenerPedidosConError() {
        return pedidoDao.obtenerPorEstado("error");
    }

    public Pedido obtenerPorId(int id) {
        return pedidoDao.obtenerPorId(id);
    }

    public void guardar(Pedido pedido) {
        pedidoDao.insert(pedido);
    }

    public void actualizarEstado(int id, String estado) {
        pedidoDao.actualizarEstado(id, estado);
    }

    public int sincronizarPendientes() {
        List<Pedido> pendientes = obtenerPedidosLocales();
        int sincronizados = 0;

        for (Pedido pedido : pendientes) {
            if (enviarPedidoApi(pedido)) {
                actualizarEstado(pedido.getId(), "enviado");
                sincronizados++;
            } else {
                actualizarEstado(pedido.getId(), "error");
            }
        }

        return sincronizados;
    }

    public boolean reenviarPedido(int pedidoId) {
        Pedido pedido = obtenerPorId(pedidoId);
        if (pedido != null) {
            return enviarPedidoApi(pedido);
        }
        return false;
    }

    private boolean enviarPedidoApi(Pedido pedido) {
        try {
            Thread.sleep(1000);

            return Math.random() > 0.2;
        } catch (Exception e) {
            return false;
        }
    }

    public void eliminar(int id) {
        pedidoDao.eliminar(id);
    }

    public int contar() {
        return pedidoDao.contar();
    }

}
