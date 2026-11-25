package com.sheirenarnelia.pondokbukitcoffee;

public class HeroSlide {
    private final int imageResId;
    private final String kicker;
    private final String title;
    private final String subtitle;

    public HeroSlide(int imageResId, String kicker, String title, String subtitle) {
        this.imageResId = imageResId;
        this.kicker = kicker;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getKicker() {
        return kicker;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
