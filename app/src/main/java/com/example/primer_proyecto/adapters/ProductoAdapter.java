package com.example.primer_proyecto.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.primer_proyecto.R;
import com.example.primer_proyecto.api.models.Inventario;

import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {
    private List<Inventario> productos;
    private OnProductoClickListener listener;

    public interface OnProductoClickListener {
        void onProductoClick(Inventario producto);
    }

    public ProductoAdapter(List<Inventario> productos, OnProductoClickListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Inventario producto = productos.get(position);
        holder.bind(producto, listener);
    }

    @Override
    public int getItemCount() {
        return productos != null ? productos.size() : 0;
    }

    public void setProductos(List<Inventario> productos) {
        this.productos = productos;
        notifyDataSetChanged();
    }

    public void filterList(List<Inventario> filteredList) {
        this.productos = filteredList;
        notifyDataSetChanged();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre, tvDescripcion, tvPrecio, tvStock, tvEstado;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }

        public void bind(Inventario producto, OnProductoClickListener listener) {
            tvNombre.setText(producto.getNombreProducto());
            tvPrecio.setText(String.format(Locale.getDefault(), "$%.2f", producto.getPrecio()));

            if (producto.getStockActual() <= 0) {
                // PRODUCTO SIN STOCK
                itemView.setAlpha(0.5f);
                tvStock.setTextColor(Color.RED);
                tvStock.setText("SIN STOCK");

                itemView.setOnClickListener(v -> {
                    Toast.makeText(v.getContext(), "Producto sin stock disponible", Toast.LENGTH_SHORT).show();
                });
            } else {
                // PRODUCTO CON STOCK
                itemView.setAlpha(1.0f);
                tvStock.setTextColor(Color.GRAY);
                tvStock.setText(String.format(Locale.getDefault(), "%d unidades", producto.getStockActual()));

                // ✅ CORRECTO: El listener solo se ejecuta al hacer click
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onProductoClick(producto);
                    }
                });
            }
        }

            /*if (producto.isActivo()) {
                tvEstado.setText("ACTIVO");
                tvEstado.setBackgroundResource(R.drawable.bg_status_active);
            } else {
                tvEstado.setText("INACTIVO");
                tvEstado.setBackgroundResource(R.drawable.bg_status_inactive);
            }*/


    }

}
