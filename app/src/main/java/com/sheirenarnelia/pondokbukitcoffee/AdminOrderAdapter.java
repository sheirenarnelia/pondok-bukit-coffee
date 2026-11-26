package com.sheirenarnelia.pondokbukitcoffee;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    public interface OnStatusClickListener {
        void onUpdateStatus(Order order);
    }

    private final List<Order> orderList;
    private final OnStatusClickListener listener;

    public AdminOrderAdapter(List<Order> orderList, OnStatusClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder h, int position) {
        Order o = orderList.get(position);

        h.tvOrderId.setText("Order #" + o.getShortId());
        h.tvDate.setText(o.getFormattedDate());
        h.tvCustomer.setText("Customer: " + (o.getUserName() == null ? "-" : o.getUserName()));
        h.tvType.setText(o.getOrderInfo());
        h.tvItems.setText(o.getItemsSummary());
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

        String action = o.getNextActionLabel();
        if (action != null) {
            h.btnUpdateStatus.setVisibility(View.VISIBLE);
            h.btnUpdateStatus.setText(action);
            h.btnUpdateStatus.setOnClickListener(v -> {
                if (listener != null) listener.onUpdateStatus(o);
            });
        } else {
            h.btnUpdateStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvDate, tvCustomer, tvType, tvItems, tvNote, tvTotal;
        Button btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvType = itemView.findViewById(R.id.tvType);
            tvItems = itemView.findViewById(R.id.tvItems);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}