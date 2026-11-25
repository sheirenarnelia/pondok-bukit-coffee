package com.sheirenarnelia.pondokbukitcoffee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HeroSliderAdapter extends RecyclerView.Adapter<HeroSliderAdapter.HeroViewHolder> {

    private final List<HeroSlide> slides;

    public HeroSliderAdapter(List<HeroSlide> slides) {
        this.slides = slides;
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hero_slider, parent, false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        HeroSlide slide = slides.get(position);
        holder.ivHeroImage.setImageResource(slide.getImageResId());
        holder.tvHeroTag.setText(slide.getTag());
        holder.tvSlideTitle.setText(slide.getTitle());
        holder.tvSlideSubtitle.setText(slide.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return slides != null ? slides.size() : 0;
    }

    static class HeroViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHeroImage;
        TextView tvHeroTag, tvSlideTitle, tvSlideSubtitle;

        public HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHeroImage = itemView.findViewById(R.id.ivHeroImage);
            tvHeroTag = itemView.findViewById(R.id.tvHeroTag);
            tvSlideTitle = itemView.findViewById(R.id.tvSlideTitle);
            tvSlideSubtitle = itemView.findViewById(R.id.tvSlideSubtitle);
        }
    }
}
