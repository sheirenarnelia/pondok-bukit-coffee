package com.sheirenarnelia.pondokbukitcoffee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartItem implements Serializable {
    private String menuId;
    private String name;
    private String size; // "Regular" or "Large"
    private List<String> toppings;
    private String notes;
    private int basePrice;
    private int quantity;

    public CartItem(String menuId, String name, String size, List<String> toppings, String notes, int basePrice, int quantity) {
        this.menuId = menuId;
        this.name = name;
        this.size = size;
        this.toppings = toppings != null ? toppings : new ArrayList<>();
        this.notes = notes != null ? notes : "";
        this.basePrice = basePrice;
        this.quantity = quantity;
    }

    public String getMenuId() { return menuId; }
    public String getName() { return name; }
    public String getSize() { return size; }
    public List<String> getToppings() { return toppings; }
    public String getNotes() { return notes; }
    public int getBasePrice() { return basePrice; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        int price = basePrice;
        if ("Large".equalsIgnoreCase(size)) {
            price += 5000;
        }
        if (toppings != null) {
            price += (toppings.size() * 5000); // Rp 5,000 per topping
        }
        return price;
    }

    public int getSubtotal() {
        return getUnitPrice() * quantity;
    }

    public String getCustomizationSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Size: ").append(size);
        if (toppings != null && !toppings.isEmpty()) {
            sb.append(" • ").append(String.join(", ", toppings));
        }
        if (notes != null && !notes.trim().isEmpty()) {
            sb.append("\nNote: \"").append(notes).append("\"");
        }
        return sb.toString();
    }
}
