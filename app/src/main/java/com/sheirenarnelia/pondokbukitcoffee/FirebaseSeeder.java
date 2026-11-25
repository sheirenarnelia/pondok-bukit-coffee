package com.sheirenarnelia.pondokbukitcoffee;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class FirebaseSeeder {

    public interface SeedCallback {
        void onComplete();
    }

    public static void seedMenuDataIfEmpty(FirebaseFirestore db, SeedCallback callback) {
        db.collection("menus").limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                List<MenuItem> defaultItems = createDefaultMenuList();
                WriteBatch batch = db.batch();

                for (MenuItem item : defaultItems) {
                    batch.set(db.collection("menus").document(item.getId()), item);
                }

                batch.commit().addOnCompleteListener(task -> {
                    if (callback != null) {
                        callback.onComplete();
                    }
                });
            } else {
                if (callback != null) {
                    callback.onComplete();
                }
            }
        }).addOnFailureListener(e -> {
            if (callback != null) {
                callback.onComplete();
            }
        });
    }

    public static List<MenuItem> createDefaultMenuList() {
        List<MenuItem> list = new ArrayList<>();

        // ☕ Coffee Series
        list.add(new MenuItem("menu_1", "Creamy Latte Bukit", "Coffee", 25000,
                "Double-shot house espresso layered over cold velvety milk with a smooth caramel finish.",
                "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=500"));
        list.add(new MenuItem("menu_2", "Aren Latte Bukit", "Coffee", 23000,
                "Classic espresso combined with fresh milk and authentic organic palm sugar.",
                "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=500"));
        list.add(new MenuItem("menu_3", "Butterscotch Creamy", "Coffee", 25000,
                "Smooth espresso infused with warm buttery butterscotch notes and creamy foam.",
                "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=500"));
        list.add(new MenuItem("menu_4", "Caffe Latte", "Coffee", 23000,
                "Balanced double espresso topped with silky steamed fresh milk.",
                "https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?w=500"));
        list.add(new MenuItem("menu_5", "Americano Peach", "Coffee", 22000,
                "Crisp chilled black coffee with a refreshing hint of fragrant peach fruit.",
                "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500"));

        // 🍵 Non-Coffee Series
        list.add(new MenuItem("menu_6", "Matcha Sea Salt", "Non-Coffee", 25000,
                "Premium Japanese matcha latte topped with velvety sea salt cream cheese.",
                "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=500"));
        list.add(new MenuItem("menu_7", "Choco Cloud Cheese", "Non-Coffee", 25000,
                "Decadent dark chocolate paired with smooth milk and savory cream cheese cloud.",
                "https://images.unsplash.com/photo-1542990253-0d0f5be5f0ed?w=500"));
        list.add(new MenuItem("menu_8", "Lychee Yakult", "Non-Coffee", 23000,
                "Tangy probiotic Yakult drink blended with sweet aromatic lychee puree.",
                "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500"));
        list.add(new MenuItem("menu_9", "Avocado Milk", "Non-Coffee", 25000,
                "Creamy ripe avocado blended smooth with fresh full-cream milk.",
                "https://images.unsplash.com/photo-1546173159-315724a31696?w=500"));
        list.add(new MenuItem("menu_10", "Lemon Tea", "Non-Coffee", 18000,
                "Refreshing brewed black tea with a bright splash of freshly squeezed lemon.",
                "https://images.unsplash.com/photo-1556881286-fc6915169721?w=500"));

        // 🍛 Main Course Series
        list.add(new MenuItem("menu_11", "Rice Bowl Katsu Cheese", "Main Course", 25000,
                "Warm steamed rice topped with golden crispy chicken katsu and savory melted cheese sauce.",
                "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=500"));
        list.add(new MenuItem("menu_12", "Nasi Goreng Pondok", "Main Course", 23000,
                "Authentic Indonesian fried rice served with sunny-side-up egg, pickles, and crispy crackers.",
                "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=500"));
        list.add(new MenuItem("menu_13", "Beef Black Pepper", "Main Course", 30000,
                "Tender sliced beef glazed in aromatic crushed black pepper sauce over fragrant white rice.",
                "https://images.unsplash.com/photo-1544025162-d76694265947?w=500"));
        list.add(new MenuItem("menu_14", "Indomie Katsu Matah", "Main Course", 23000,
                "Iconic fried noodles paired with crispy chicken cutlet and fresh spicy Balinese sambal.",
                "https://images.unsplash.com/photo-1612927601601-6638404737ce?w=500"));

        // 🥐 Snacks Series
        list.add(new MenuItem("menu_15", "Mix Platter", "Snacks", 30000,
                "Crispy french fries, savory beef sausages, and golden chicken nuggets.",
                "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=500"));
        list.add(new MenuItem("menu_16", "Chicken Mentai Risol", "Snacks", 25000,
                "Crispy breaded spring roll stuffed with shredded chicken and torch-glazed mentai sauce.",
                "https://images.unsplash.com/photo-1541592106381-b31e9677c0e5?w=500"));
        list.add(new MenuItem("menu_17", "Pisang Goreng Keju", "Snacks", 17000,
                "Caramelized fried banana fritters topped with shredded cheddar cheese and sweet condensed milk.",
                "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?w=500"));

        return list;
    }
}
