package com.example.primer_proyecto.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.primer_proyecto.api.models.Cliente;

import java.util.List;

@Dao
public interface ClienteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Cliente> clientes);

    @Query("SELECT * FROM cliente ORDER BY Nombre ASC")
    LiveData<List<Cliente>> obtenerTodos();

    @Query("SELECT * FROM cliente WHERE IdCliente = :id")
    Cliente obtenerPorId(int id);

    @Query("SELECT COUNT(*) FROM cliente")
    int contarClientes();

    @Query("DELETE FROM cliente")
    void eliminarTodos();

}
