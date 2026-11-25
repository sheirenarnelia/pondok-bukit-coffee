package com.sheirenarnelia.pondokbukitcoffee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HeroSliderAdapter extends RecyclerView.Adapter<HeroSliderAdapter.HeroViewHolder> {

    private final List<Integer> imageResIds;

    public HeroSliderAdapter(List<Integer> imageResIds) {
        this.imageResIds = imageResIds;
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hero_slider, parent, false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        int resId = imageResIds.get(position);
        holder.ivHeroImage.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return imageResIds != null ? imageResIds.size() : 0;
    }

    static class HeroViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHeroImage;

        public HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHeroImage = itemView.findViewById(R.id.ivHeroImage);
        }
    }
}
