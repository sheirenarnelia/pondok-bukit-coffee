package com.sheirenarnelia.pondokbukitcoffee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    private final List<CartItem> cartList;
    private final OnCartChangeListener listener;
    private final NumberFormat currencyFormatter;

    public CartAdapter(List<CartItem> cartList, OnCartChangeListener listener) {
        this.cartList = cartList;
        this.listener = listener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.tvItemName.setText(item.getName());
        holder.tvCustomization.setText(item.getCustomizationSummary());
        holder.tvItemPrice.setText(currencyFormatter.format(item.getSubtotal()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Increase Quantity
        holder.btnPlus.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                CartManager.getInstance().updateQuantity(currentPos, item.getQuantity() + 1);
                notifyItemChanged(currentPos);
                if (listener != null) listener.onCartChanged();
            }
        });

        // Decrease Quantity
        holder.btnMinus.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                if (item.getQuantity() > 1) {
                    CartManager.getInstance().updateQuantity(currentPos, item.getQuantity() - 1);
                    notifyItemChanged(currentPos);
                } else {
                    CartManager.getInstance().removeItem(currentPos);
                    notifyItemRemoved(currentPos);
                }
                if (listener != null) listener.onCartChanged();
            }
        });

        // Delete Item
        holder.ivDelete.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                CartManager.getInstance().removeItem(currentPos);
                notifyItemRemoved(currentPos);
                if (listener != null) listener.onCartChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvCustomization, tvItemPrice, tvQuantity;
        ImageButton btnPlus, btnMinus;
        ImageView ivDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvCustomization = itemView.findViewById(R.id.tvCustomization);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
