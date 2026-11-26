
package com.sheirenarnelia.pondokbukitcoffee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminMenuAdapter extends
        RecyclerView.Adapter<AdminMenuAdapter.MenuViewHolder> {

    public interface MenuActionListener {
        void onEdit(MenuItem item);
        void onDelete(MenuItem item);
    }

    private final List<MenuItem> menuList;
    private final MenuActionListener listener;

    public AdminMenuAdapter(List<MenuItem> menuList,
                            MenuActionListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_menu,
                        parent, false);

        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MenuViewHolder holder, int position) {

        MenuItem item = menuList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvCategory.setText(item.getCategory());
        holder.tvDescription.setText(item.getDescription());

        String price = NumberFormat
                .getNumberInstance(new Locale("id", "ID"))
                .format(item.getPrice());

        holder.tvPrice.setText("Rp " + price);

        holder.btnEdit.setOnClickListener(v ->
                listener.onEdit(item));

        holder.btnDelete.setOnClickListener(v ->
                listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCategory, tvPrice, tvDescription;
        MaterialButton btnEdit, btnDelete;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvAdminMenuName);

            tvCategory = itemView.findViewById(
                    R.id.tvAdminMenuCategory);

            tvPrice = itemView.findViewById(
                    R.id.tvAdminMenuPrice);

            tvDescription = itemView.findViewById(
                    R.id.tvAdminMenuDescription);

            btnEdit = itemView.findViewById(R.id.btnEditMenu);

            btnDelete = itemView.findViewById(R.id.btnDeleteMenu);
        }
    }
}
