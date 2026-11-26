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
    }

    public static List<MenuItem> createDefaultMenuList() {
        List<MenuItem> list = new ArrayList<>();

        // ☕ 1. Coffee Series (6 Items)
        list.add(new MenuItem("menu_1", "Creamy Latte Bukit", "Coffee", 25000,
                "Double-shot house espresso layered over cold velvety milk with a smooth caramel finish.",
                "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=600"));
        list.add(new MenuItem("menu_2", "Aren Latte Bukit", "Coffee", 23000,
                "Classic espresso combined with fresh milk and authentic organic palm sugar.",
                "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=600"));
        list.add(new MenuItem("menu_3", "Butterscotch Creamy", "Coffee", 25000,
                "Smooth espresso infused with warm buttery butterscotch notes and creamy foam.",
                "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=600"));
        list.add(new MenuItem("menu_4", "Caffe Latte", "Coffee", 23000,
                "Balanced double espresso topped with silky steamed fresh milk.",
                "https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?w=600"));
        list.add(new MenuItem("menu_5", "Americano Peach", "Coffee", 22000,
                "Crisp chilled black coffee with a refreshing hint of fragrant peach fruit.",
                "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600"));
        list.add(new MenuItem("menu_6", "Caramel Macchiato", "Coffee", 25000,
                "Freshly steamed milk with vanilla-flavored syrup marked with espresso and topped with caramel drizzle.",
                "https://images.unsplash.com/photo-1485808191679-5f86510681a2?w=600"));

        // 🍵 2. Non-Coffee Series (6 Items)
        list.add(new MenuItem("menu_7", "Matcha Sea Salt", "Non-Coffee", 25000,
                "Vibrant ceremonial grade Uji matcha topped with velvety, thick sea salt cream cheese.",
                "https://images.unsplash.com/photo-1515823064-d6e0c04616a7?w=600"));
        list.add(new MenuItem("menu_8", "Choco Cloud Cheese", "Non-Coffee", 25000,
                "Decadent dark chocolate paired with smooth milk and savory cream cheese cloud.",
                "https://images.unsplash.com/photo-1542990253-0d0f5be5f0ed?w=600"));
        list.add(new MenuItem("menu_9", "Ice Tea", "Non-Coffee", 15000,
                "Freshly brewed premium iced black tea, naturally refreshing and invigorating.",
                "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600"));
        list.add(new MenuItem("menu_10", "Mango Juice", "Non-Coffee", 22000,
                "Sweet tropical ripe mango pureed fresh with chilled crushed ice.",
                "https://images.unsplash.com/photo-1546173159-315724a31696?w=600"));
        list.add(new MenuItem("menu_11", "Ice Lemonade", "Non-Coffee", 18000,
                "Crisp freshly squeezed lemon juice served cold over ice for a zesty thirst quencher.",
                "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=600"));
        list.add(new MenuItem("menu_12", "Strawberry Milk", "Non-Coffee", 22000,
                "Fresh chilled dairy milk layered over sweet homemade strawberry compote and fruit slices.",
                "https://images.unsplash.com/photo-1589733955941-5eeaf752f6dd?w=600"));

        // 🍛 3. Main Course Series (5 Items)
        list.add(new MenuItem("menu_13", "Rice Bowl Katsu Cheese", "Main Course", 25000,
                "Warm steamed rice topped with golden crispy chicken katsu and savory melted cheese sauce.",
                "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=600"));
        list.add(new MenuItem("menu_14", "Nasi Goreng Pondok", "Main Course", 23000,
                "Authentic Indonesian fried rice served with sunny-side-up egg, pickles, and crispy crackers.",
                "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=600"));
        list.add(new MenuItem("menu_15", "Beef Black Pepper", "Main Course", 30000,
                "Tender sliced beef glazed in aromatic crushed black pepper sauce over fragrant white rice.",
                "https://images.unsplash.com/photo-1544025162-d76694265947?w=600"));
        list.add(new MenuItem("menu_16", "Indomie Goreng Special", "Main Course", 18000,
                "Classic Indonesian fried noodles served with crispy fried egg and savory toppings.",
                "https://images.unsplash.com/photo-1612927601601-6638404737ce?w=600"));
        list.add(new MenuItem("menu_17", "Spaghetti Bolognese", "Main Course", 22000,
                "Al dente spaghetti pasta in slow-cooked minced beef with savory Italian herb tomato sauce.",
                "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=600"));

        // 🥐 4. Snacks Series (4 Items)
        list.add(new MenuItem("menu_18", "Mix Platter", "Snacks", 30000,
                "Crispy french fries, savory beef sausages, and golden fried chicken nuggets with dipping sauce.",
                "https://images.unsplash.com/photo-1561758033-d89a9ad46330?w=600"));
        list.add(new MenuItem("menu_19", "French Fries", "Snacks", 18000,
                "Crispy golden shoestring fries sprinkled with sea salt and served with chili sauce.",
                "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=600"));
        list.add(new MenuItem("menu_20", "Toast Sandwich", "Snacks", 22000,
                "Golden buttery toasted bread layered with savory smoked meat and melted cheddar cheese.",
                "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?w=600"));
        list.add(new MenuItem("menu_21", "Dimsum Kukus (5 pcs)", "Snacks", 25000,
                "Steamed tender chicken and shrimp dim sum dumplings served hot with chili oil.",
                "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=600"));

        return list;
    }
}