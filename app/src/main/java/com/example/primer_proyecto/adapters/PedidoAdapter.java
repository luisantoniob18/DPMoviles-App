package com.example.primer_proyecto.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.primer_proyecto.R;
import com.example.primer_proyecto.api.models.Pedido;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {
    private List<Pedido> pedidos;
    private OnPedidoClickListener listener;

    public interface OnPedidoClickListener {
        void onVerDetallesClick(Pedido pedido);
        void onReenviarClick(Pedido pedido);
    }

    public PedidoAdapter(List<Pedido> pedidos, OnPedidoClickListener listener) {
        this.pedidos = pedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.bind(pedido, listener);
    }

    @Override
    public int getItemCount() {
        return pedidos != null ? pedidos.size() : 0;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
        notifyDataSetChanged();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIdPedido, tvEstado, tvCliente, tvFecha, tvProductos, tvTotal, tvUbicacion;
        private Button btnReenviar, btnDetalles;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdPedido = itemView.findViewById(R.id.tvIdPedido);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvProductos = itemView.findViewById(R.id.tvProductos);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            btnReenviar = itemView.findViewById(R.id.btnReenviar);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
        }

        public void bind(Pedido pedido, OnPedidoClickListener listener) {
            tvIdPedido.setText(String.format("Pedido #%03d", pedido.getId()));
            tvCliente.setText("Cliente: " + pedido.getNombreCliente());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvFecha.setText("Fecha: " + sdf.format(pedido.getFechaCreacion()));

            int numProductos = pedido.getProductos() != null ? pedido.getProductos().size() : 0;
            tvProductos.setText(numProductos + " productos");
            //tvTotal.setText(String.format("Total: $%.2f", pedido.getTotal()));

            tvUbicacion.setText(String.format("📍 Lat: %.4f, Long: %.4f",
                    pedido.getLatitud(), pedido.getLongitud()));

            configurarEstado(pedido.getEstado());

            btnDetalles.setOnClickListener(v -> {
                if (listener != null) listener.onVerDetallesClick(pedido);
            });

            btnReenviar.setOnClickListener(v -> {
                if (listener != null) listener.onReenviarClick(pedido);
            });


        }

        private void configurarEstado(String estado) {
            switch (estado.toLowerCase()) {
                case "enviado":
                    tvEstado.setText("ENVIADO");
                    tvEstado.setBackgroundResource(R.drawable.bg_status_active);
                    btnReenviar.setVisibility(View.GONE);
                    break;
                case "error":
                    tvEstado.setText("ERROR");
                    tvEstado.setBackgroundResource(R.drawable.bg_status_inactive);
                    btnReenviar.setVisibility(View.VISIBLE);
                    break;
                default:
                    tvEstado.setText("LOCAL");
                    tvEstado.setBackgroundResource(R.drawable.bg_status_local);
                    btnReenviar.setVisibility(View.VISIBLE);
                    break;
            }
        }
        }

}
