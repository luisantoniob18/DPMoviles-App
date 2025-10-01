package com.example.primer_proyecto.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.primer_proyecto.api.models.Pedido;

import java.util.List;

@Dao
public interface PedidoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Pedido pedido);

    @Query("SELECT * FROM pedidos ORDER BY fechaCreacion DESC")
    List<Pedido> obtenerTodos();

    @Query("SELECT * FROM pedidos WHERE estado = :estado ORDER BY fechaCreacion DESC")
    List<Pedido> obtenerPorEstado(String estado);

    @Query("SELECT * FROM pedidos WHERE id = :id")
    Pedido obtenerPorId(int id);

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    void actualizarEstado(int id, String estado);

    @Query("DELETE FROM pedidos WHERE id = :id")
    void eliminar(int id);

    @Query("SELECT COUNT(*) FROM pedidos")
    int contar();

    @Query("DELETE FROM pedidos")
    void eliminarTodos();

    @Query("SELECT * FROM pedidos WHERE IdEmpleado = :idEmpleado")
    List<Pedido> obtenerPorEmpleado(int idEmpleado);

    @Update
    void update(Pedido pedido);
}
