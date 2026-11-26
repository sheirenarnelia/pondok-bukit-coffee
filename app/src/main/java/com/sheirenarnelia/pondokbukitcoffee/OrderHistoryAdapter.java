package com.sheirenarnelia.pondokbukitcoffee;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.HistoryViewHolder> {

    private final List<Order> orderList;

    public OrderHistoryAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder h, int position) {
        Order o = orderList.get(position);

        h.tvOrderId.setText("Order #" + o.getShortId());
        h.tvDate.setText(o.getFormattedDate());
        h.tvItems.setText(o.getItemsSummary());
        h.tvType.setText(o.getOrderInfo());
        h.tvTotal.setText(Order.formatRupiah(o.getTotal()));

        if (o.getNote() != null && !o.getNote().isEmpty()) {
            h.tvNote.setVisibility(View.VISIBLE);
            h.tvNote.setText("Note: " + o.getNote());
        } else {
            h.tvNote.setVisibility(View.GONE);
        }

        h.tvStatus.setText(o.getStatusLabel());
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(40f);
        bg.setColor(Order.statusColor(o.getStatus()));
        h.tvStatus.setBackground(bg);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvDate, tvItems, tvType, tvNote, tvTotal;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvItems = itemView.findViewById(R.id.tvItems);
            tvType = itemView.findViewById(R.id.tvType);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}