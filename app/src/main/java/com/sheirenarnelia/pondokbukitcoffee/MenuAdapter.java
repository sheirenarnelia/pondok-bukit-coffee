package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private final Context context;
    private List<MenuItem> menuList;
    private final NumberFormat rupiahFormat;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MenuAdapter(Context context, List<MenuItem> menuList) {
        this.context = context;
        this.menuList = menuList != null ? menuList : new ArrayList<>();
        this.rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        this.rupiahFormat.setMaximumFractionDigits(0);
    }

    public void updateList(List<MenuItem> newList) {
        this.menuList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_card, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuList.get(position);

        holder.tvMenuName.setText(item.getName());
        holder.tvMenuPrice.setText("Rp " + String.format(Locale.GERMAN, "%,d", item.getPrice()));

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.ivMenuPhoto);
        } else {
            holder.ivMenuPhoto.setImageResource(R.drawable.ic_launcher_background);
        }

        View.OnClickListener clickAction = v -> {
            if (listener != null) {
                listener.onItemClick(item);
            } else {
                Intent intent = new Intent(context, DetailMenuActivity.class);
                intent.putExtra("menu_item", item);
                context.startActivity(intent);
            }
        };

        holder.itemView.setOnClickListener(clickAction);
        holder.btnAdd.setOnClickListener(clickAction);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMenuPhoto;
        TextView tvMenuName;
        TextView tvMenuPrice;
        MaterialButton btnAdd;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMenuPhoto = itemView.findViewById(R.id.ivMenuPhoto);
            tvMenuName = itemView.findViewById(R.id.tvMenuName);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}
