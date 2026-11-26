package com.sheirenarnelia.pondokbukitcoffee;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();

    private CartManager() {
        // Cart starts empty by default; items are added from DetailMenuActivity
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void addItem(CartItem item) {
        cartItems.add(item);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void updateQuantity(int position, int newQty) {
        if (position >= 0 && position < cartItems.size()) {
            if (newQty <= 0) {
                cartItems.remove(position);
            } else {
                cartItems.get(position).setQuantity(newQty);
            }
        }
    }

    public int getSubtotal() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getTax() {
        return (int) (getSubtotal() * 0.10); // 10% tax
    }

    public int getTotalPrice() {
        return getSubtotal() + getTax();
    }

    public void clearCart() {
        cartItems.clear();
    }
}
