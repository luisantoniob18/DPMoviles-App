package com.example.primer_proyecto.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.primer_proyecto.api.models.Inventario;

import java.util.List;

@Dao
public interface InventarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Inventario> productos);

    @Query("SELECT * FROM inventario ORDER BY NombreProducto ASC")
    List<Inventario> obtenerTodos();

    @Query("SELECT * FROM inventario WHERE StockActual > 0 ORDER BY NombreProducto ASC")
    List<Inventario> obtenerConStock();

    @Query("SELECT * FROM inventario WHERE StockActual <= 0 ORDER BY NombreProducto ASC")
    List<Inventario> obtenerSinStock();

    @Query("SELECT * FROM inventario WHERE NombreProducto LIKE :nombre ORDER BY NombreProducto ASC")
    List<Inventario> buscarPorNombre(String nombre);

    @Query("SELECT * FROM inventario WHERE IdProducto = :id")
    Inventario obtenerPorId(int id);

    @Query("UPDATE inventario SET StockActual = :nuevoStock WHERE IdProducto = :id")
    void actualizarStock(int id, int nuevoStock);

    @Query("SELECT COUNT(*) FROM inventario")
    int contar();

    @Query("DELETE FROM inventario")
    void eliminarTodos();

}
