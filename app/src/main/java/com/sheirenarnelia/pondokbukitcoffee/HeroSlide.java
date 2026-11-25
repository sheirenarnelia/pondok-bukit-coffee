package com.sheirenarnelia.pondokbukitcoffee;

public class HeroSlide {
    private final int imageResId;
    private final String tag;
    private final String title;
    private final String subtitle;

    public HeroSlide(int imageResId, String tag, String title, String subtitle) {
        this.imageResId = imageResId;
        this.tag = tag;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
