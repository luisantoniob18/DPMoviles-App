package com.example.primer_proyecto.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.primer_proyecto.api.models.Cliente;
import com.example.primer_proyecto.api.models.Inventario;
import com.example.primer_proyecto.api.models.Pedido;
import com.example.primer_proyecto.database.dao.ClienteDao;
import com.example.primer_proyecto.database.dao.InventarioDao;
import com.example.primer_proyecto.database.dao.PedidoDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Cliente.class, Inventario.class, Pedido.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ClienteDao clienteDao();

    public abstract InventarioDao inventarioDao();

    public abstract PedidoDao pedidoDao();

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class,
                                    "ventas_database")
                            .fallbackToDestructiveMigration(false)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
