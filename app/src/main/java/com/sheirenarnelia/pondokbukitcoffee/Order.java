package com.sheirenarnelia.pondokbukitcoffee;

import android.graphics.Color;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Order data used by Admin Order List and User Order History.
 * Planned Firestore collection: "orders".
 * Status values stored in Firestore: Diproses -> Siap -> Selesai.
 */
public class Order {

    public static final String STATUS_DIPROSES = "Diproses";
    public static final String STATUS_SIAP = "Siap";
    public static final String STATUS_SELESAI = "Selesai";

    private String id;
    private String userId;
    private String userName;
    private String orderType;      // "Dine-in" or "Pickup"
    private String tableNumber;
    private String pickupName;
    private String paymentMethod;
    private String note;
    private String status;
    private double total;
    private Date createdAt;
    private List<Map<String, Object>> items; // each: name, quantity, size, price, toppings, notes

    // Empty constructor (needed later for Firestore)
    public Order() {}

    // Constructor for sample data
    public Order(String id, String userName, String orderType, String tableNumber,
                 String pickupName, String note, String status, double total,
                 Date createdAt, List<Map<String, Object>> items) {
        this.id = id;
        this.userName = userName;
        this.orderType = orderType;
        this.tableNumber = tableNumber;
        this.pickupName = pickupName;
        this.note = note;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
        this.items = items;
    }

    // ---------- Getters and setters ----------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }
    public String getPickupName() { return pickupName; }
    public void setPickupName(String pickupName) { this.pickupName = pickupName; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }

    // ---------- Helpers for display ----------

    /** "001" for sample data, first 6 characters (uppercase) for Firestore ids. */
    public String getShortId() {
        if (id == null) return "-";
        return id.length() <= 6 ? id : id.substring(0, 6).toUpperCase();
    }

    public String getFormattedDate() {
        if (createdAt == null) return "-";
        return new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID")).format(createdAt);
    }

    /** Example: "Dine-in • Table 05" or "Pickup • Budi". */
    public String getOrderInfo() {
        String type = orderType == null ? "-" : orderType;
        if ("Dine-in".equalsIgnoreCase(type) && tableNumber != null && !tableNumber.isEmpty()) {
            return type + " • Table " + tableNumber;
        }
        if ("Pickup".equalsIgnoreCase(type) && pickupName != null && !pickupName.isEmpty()) {
            return type + " • " + pickupName;
        }
        return type;
    }

    /** Example: "Signature Latte × 2" plus a line for size and toppings. */
    public String getItemsSummary() {
        if (items == null || items.isEmpty()) return "-";
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> m : items) {
            Object name = m.get("name");
            Object qty = m.get("quantity");
            int q = (qty instanceof Number) ? ((Number) qty).intValue() : 1;
            sb.append(name != null ? name : "Item").append(" × ").append(q);

            StringBuilder custom = new StringBuilder();
            Object size = m.get("size");
            if (size != null && !size.toString().isEmpty()) custom.append(size);
            Object toppings = m.get("toppings");
            if (toppings instanceof List && !((List<?>) toppings).isEmpty()) {
                if (custom.length() > 0) custom.append(" • ");
                custom.append(String.join(", ", toStringList((List<?>) toppings)));
            }
            if (custom.length() > 0) sb.append("\n   ").append(custom);

            Object itemNote = m.get("notes");
            if (itemNote != null && !itemNote.toString().isEmpty()) {
                sb.append("\n   Note: ").append(itemNote);
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    private static List<String> toStringList(List<?> list) {
        List<String> out = new java.util.ArrayList<>();
        for (Object o : list) out.add(String.valueOf(o));
        return out;
    }

    /** English label shown in the UI. */
    public String getStatusLabel() {
        if (STATUS_SIAP.equals(status)) return "Ready";
        if (STATUS_SELESAI.equals(status)) return "Completed";
        return "Processing";
    }

    /** Next status value to save, or null when the order is already completed. */
    public String getNextStatus() {
        if (STATUS_DIPROSES.equals(status)) return STATUS_SIAP;
        if (STATUS_SIAP.equals(status)) return STATUS_SELESAI;
        return null;
    }

    /** Admin button text, or null when there is no next step. */
    public String getNextActionLabel() {
        if (STATUS_DIPROSES.equals(status)) return "Mark as Ready";
        if (STATUS_SIAP.equals(status)) return "Complete Order";
        return null;
    }

    public static int statusColor(String status) {
        if (STATUS_SIAP.equals(status)) return Color.parseColor("#2E7D32");     // green
        if (STATUS_SELESAI.equals(status)) return Color.parseColor("#757575");  // grey
        return Color.parseColor("#E65100");                                     // orange
    }

    public static String formatRupiah(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(amount);
    }

    /** Helper to build one item for sample data. */
    public static Map<String, Object> line(String name, int quantity, String size) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("quantity", quantity);
        m.put("size", size);
        return m;
    }
}