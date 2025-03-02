package org.example.services;
import org.example.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.example.services.Utils.displayItemsAsTable;

public class OrderService {
    private final List<Map<String, Object>> cart;

    public OrderService() {
        this.cart = new ArrayList<>();
    }

    public List<Map<String, Object>> getMenuItems() {
        List<Map<String, Object>> menuItems = new ArrayList<>();
        String query = "SELECT item_id, name, category, sell_price, discount FROM menuitemsadmin";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> menuItem = new HashMap<>();
                menuItem.put("item_id", rs.getInt("item_id"));
                menuItem.put("name", rs.getString("name"));
                menuItem.put("category", rs.getString("category"));
                menuItem.put("sell_price", rs.getDouble("sell_price"));
                menuItem.put("discount", rs.getDouble("discount"));
                menuItems.add(menuItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    public void displayItems() {
        List<Map<String, Object>> items = getMenuItems();
        displayItemsAsTable(items);
    }

    public List<Map<String, Object>> getAllOrders() {
        List<Map<String, Object>> allOrders = new ArrayList<>();
        String query = "SELECT order_id, item_id, quantity, total_price FROM orders";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("order_id", rs.getInt("order_id"));
                order.put("item_id", rs.getInt("item_id"));
                order.put("quantity", rs.getInt("quantity"));
                order.put("total_price", rs.getDouble("total_price"));
                allOrders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allOrders;
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT name FROM categories ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void displayItemsByCategory(String selectedCategory) {
        List<Map<String, Object>> items = getMenuItems();
        List<Map<String, Object>> itemsByCategory = new ArrayList<>();

        for (Map<String, Object> item : items) {
            if (item.get("category").equals(selectedCategory)) {
                itemsByCategory.add(item);
            }
        }

        displayItemsAsTable(itemsByCategory);
    }

    public Map<String, Object> getItemById(int itemId) {
        List<Map<String, Object>> items = getMenuItems();
        for (Map<String, Object> item : items) {
            if ((int) item.get("item_id") == itemId) {
                return item;
            }
        }
        return null;
    }

    public void addItemToCart(Map<String, Object> item, int quantity) {
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("item_id", item.get("item_id"));
        cartItem.put("name", item.get("name"));
        cartItem.put("quantity", quantity);
        cartItem.put("sell_price", item.get("sell_price"));
        cartItem.put("discount", item.get("discount"));
        cart.add(cartItem);
    }

    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        // Fetch the order ID from the database
        int orderId = fetchOrderIdFromDatabase();

        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n");
        System.out.println("                        RESTAURANT ORDERING SYSTEM                        ");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Order ID: " + orderId);
        System.out.println("Order Date: " + now);
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("%-10s %-25s %5s %15s %15s %15s%n", "ID", "Name", "Quantity", "Unit Price", "Discount", "Total Price");
        System.out.println("---------------------------------------------------------------------------");


        double grandTotal = 0;
        for (Map<String, Object> cartItem : cart) {
            int itemId = (int) cartItem.get("item_id");
            String name = (String) cartItem.get("name");
            int quantity = (int) cartItem.get("quantity");
            double sellPrice = (double) cartItem.get("sell_price");
            double discount = (double) cartItem.get("discount");
            double unitPrice = sellPrice - discount;
            double totalPrice = unitPrice * quantity;
            grandTotal += totalPrice;

            System.out.printf("%-10d %-25s %5d %15.2f %15.2f %15.2f%n",
                    itemId, name, quantity, sellPrice, discount, totalPrice);
        }


        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("%-10s %-25s %5s %15s %15s %15.2f%n", "", "Grand Total", "", "", "", grandTotal);
        System.out.println("==========================================================================");
        System.out.println("                        Thank you, Please come again!                      ");
        System.out.println("==========================================================================");
    }

    private int fetchOrderIdFromDatabase() {
        int orderId = -1;
        String query = "SELECT MAX(order_id) AS order_id FROM orders";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                orderId = rs.getInt("order_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;
    }

    public boolean updateCartItemQuantity(int itemId, int newQuantity) {
        for (Map<String, Object> item : cart) {
            if ((int) item.get("item_id") == itemId) {
                item.put("quantity", newQuantity);
                return true;
            }
        }
        return false;
    }

    public boolean removeItemFromCart(int itemId) {
        for (Map<String, Object> item : cart) {
            if ((int) item.get("item_id") == itemId) {
                cart.remove(item);
                return true;
            }
        }
        return false;
    }

    public void clearCart() {
        cart.clear();
    }

    // Place order and return the order ID
    public int placeOrder(int paymentMethod) {
        double totalPrice = 0;
        for (Map<String, Object> item : cart) {
            totalPrice += (double) item.get("sell_price") * (int) item.get("quantity") * (1 - (double) item.get("discount"));
        }

        String paymentMethodString;
        switch (paymentMethod) {
            case 1:
                paymentMethodString = "Credit Card";
                break;
            case 2:
                paymentMethodString = "PayPal";
                break;
            default:
                paymentMethodString = "Cash";
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        Timestamp orderTimestamp = Timestamp.valueOf(currentDateTime);

        String orderQuery = "INSERT INTO orders (payment_method, total_amount, order_date) VALUES (?,?,?)";
        String orderItemsQuery = "INSERT INTO order_items (order_id, item_id, quantity, sell_price, discount, order_date) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement orderItemsStmt = conn.prepareStatement(orderItemsQuery)) {

            // Insert order
            orderStmt.setString(1, paymentMethodString);
            orderStmt.setDouble(2, totalPrice);
            orderStmt.setTimestamp(3, orderTimestamp);
            orderStmt.executeUpdate();

            // Get generated order ID
            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);

                // Insert order items
                for (Map<String, Object> item : cart) {
                    orderItemsStmt.setInt(1, orderId);
                    orderItemsStmt.setInt(2, (int) item.get("item_id"));
                    orderItemsStmt.setInt(3, (int) item.get("quantity"));
                    orderItemsStmt.setDouble(4, (double) item.get("sell_price"));
                    orderItemsStmt.setDouble(5, (double) item.get("discount"));
                    orderItemsStmt.setTimestamp(6, orderTimestamp);
                    orderItemsStmt.addBatch();
                }
                orderItemsStmt.executeBatch();

                return orderId;
            } else {
                throw new SQLException("Failed to retrieve order ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Process payment using chosen payment method
    public boolean processPayment(int orderId, int paymentMethod) {
        switch (paymentMethod) {
            case 1:
                System.out.println("Processing payment through Credit Card...");
                // Add credit card payment logic here
                break;
            case 2:
                System.out.println("Processing payment through PayPal...");
                // Add PayPal payment logic here
                break;
            default:
                System.out.println("Processing payment through Cash...");
                // Add cash payment logic here
        }
        System.out.println("Payment processed successfully!");
        return true;
    }

    // Generate receipt for the order
    public void generateReceipt(int orderId, int paymentMethod) {
        String query = "SELECT order_id, total_price, order_date FROM orders WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n--- Receipt ---");
                    System.out.println("Order ID: " + rs.getInt("order_id"));
                    System.out.println("Total Price: $" + rs.getDouble("total_price"));
                    System.out.println("Order Date: " + rs.getString("order_date"));
                    System.out.println("Payment Method: " + (paymentMethod == 1 ? "Credit Card" : "Cash"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isCartEmpty() {
        return cart.isEmpty();
    }
}
