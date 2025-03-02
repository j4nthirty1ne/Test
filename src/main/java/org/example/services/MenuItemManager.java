package org.example.services;

import org.example.utils.DatabaseConnection;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.sql.*;
import java.util.*;

public class MenuItemManager {
    // ------------------ View All Menu Items ------------------
    public static void viewMenuItemsAdmin() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM menuitemsadmin ORDER BY category, name";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n--------- MENU ITEMS ---------");
                String currentCategory = "";
                Table table = null;

                int count = 1;
                while (rs.next()) {
                    String category = rs.getString("category");
                    if (!category.equals(currentCategory)) {
                        if (table != null) {
                            System.out.println(table.render());
                        }
                        currentCategory = category;
                        System.out.println("\n--- " + currentCategory + " ---");
                        table = new Table(8, BorderStyle.DOTS, ShownBorders.ALL);
                        table.addCell("No.", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Name", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Description", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Item ID", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Size", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Base Price", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Sell Price", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Discount", new CellStyle(CellStyle.HorizontalAlign.center));
                        count = 1; // Reset count for new category
                    }
                    table.addCell(String.valueOf(count++), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(rs.getString("name"), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(rs.getString("description"), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(String.valueOf(rs.getInt("item_id")), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(rs.getString("size"), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(String.format("$%.2f", rs.getDouble("base_price")), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(String.format("$%.2f", rs.getDouble("sell_price")), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(String.format("$%.2f", rs.getDouble("discount")), new CellStyle(CellStyle.HorizontalAlign.center));
                }
                if (table != null) {
                    System.out.println(table.render());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ View All Menu Items ------------------
    public static void viewMenuItemsCustomer() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT item_id, name, description, category, size, sell_price, discount FROM menuitemsadmin ORDER BY category, name";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n--------- MENU ITEMS ---------");
                String currentCategory = "";
                Table table = null;

                int count = 1;
                while (rs.next()) {
                    String category = rs.getString("category");
                    if (!category.equals(currentCategory)) {
                        if (table != null) {
                            System.out.println(table.render());
                        }
                        currentCategory = category;
                        System.out.println("\n--- " + currentCategory + " ---");
                        table = new Table(7, BorderStyle.UNICODE_BOX_WIDE, ShownBorders.ALL);
                        table.addCell("No.", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Name", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Description", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Item ID", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Size", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Sell Price", new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell("Discount", new CellStyle(CellStyle.HorizontalAlign.center));
                        count = 1; // Reset count for new category
                    }
                    table.addCell(String.valueOf(count++), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(rs.getString("name"), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(rs.getString("description"), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(String.valueOf(rs.getInt("item_id")), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(rs.getString("size"), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(String.format("$%.2f", rs.getDouble("sell_price")), new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell(String.format("$%.2f", rs.getDouble("discount")), new CellStyle(CellStyle.HorizontalAlign.center));
                }
                if (table != null) {
                    System.out.println(table.render());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ Update Menu Item ------------------
    public static void updateMenuItem(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter the Item ID to update: ");
            int itemId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter new name (press Enter to skip): ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter new description (press Enter to skip): ");
            String description = scanner.nextLine().trim();

            System.out.print("Enter new base price (press Enter to skip): ");
            String basePriceInput = scanner.nextLine().trim();
            Double basePrice = basePriceInput.isEmpty() ? null : Double.parseDouble(basePriceInput);

            System.out.print("Enter new sell price (press Enter to skip): ");
            String sellPriceInput = scanner.nextLine().trim();
            Double sellPrice = sellPriceInput.isEmpty() ? null : Double.parseDouble(sellPriceInput);

            System.out.print("Enter new discount (press Enter to skip): ");
            String discountInput = scanner.nextLine().trim();
            Double discount = discountInput.isEmpty() ? null : Double.parseDouble(discountInput);

            String sql = "UPDATE menuitemsadmin SET " +
                    (name.isEmpty() ? "" : "name = ?, ") +
                    (description.isEmpty() ? "" : "description = ?, ") +
                    (basePrice == null ? "" : "base_price = ?, ") +
                    (sellPrice == null ? "" : "sell_price = ?, ") +
                    (discount == null ? "" : "discount = ? ") +
                    "WHERE item_id = ?";
            sql = sql.replaceAll(", WHERE", " WHERE");

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int index = 1;
                if (!name.isEmpty()) pstmt.setString(index++, name);
                if (!description.isEmpty()) pstmt.setString(index++, description);
                if (basePrice != null) pstmt.setDouble(index++, basePrice);
                if (sellPrice != null) pstmt.setDouble(index++, sellPrice);
                if (discount != null) pstmt.setDouble(index++, discount);
                pstmt.setInt(index, itemId);
                pstmt.executeUpdate();
                System.out.println("Menu item updated successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ Delete Menu Item ------------------
    public static void deleteMenuItem(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter the Item ID to delete: ");
            int itemId = scanner.nextInt();

            String sql = "DELETE FROM menuitemsadmin WHERE item_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, itemId);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Menu item deleted successfully!");
                } else {
                    System.out.println("Item ID not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ Add Multiple Menu Items ------------------
    public static void addMenuItem(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter item name: ");
            String name = scanner.nextLine().trim();

            // Predefined categories
            String[] categories = {"Appetizers", "Main Course", "Beverages", "Desserts"};
            System.out.println("Choose a category:");
            for (int i = 0; i < categories.length; i++) {
                System.out.println((i + 1) + ". " + categories[i]);
            }
            int categoryChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            String category = categories[categoryChoice - 1];

            System.out.print("Enter item description (optional): ");
            String description = scanner.nextLine().trim();

            System.out.print("Enter size [(S, M, L, XL, etc.) or press Enter to skip]: ");
            String size = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter base price: ");
            double basePrice = scanner.nextDouble();

            System.out.print("Enter sell price: ");
            double sellPrice = scanner.nextDouble();

            System.out.print("Enter discount (press Enter to skip): ");
            scanner.nextLine(); // Consume newline
            String discountInput = scanner.nextLine().trim();
            double discount = discountInput.isEmpty() ? 0.0 : Double.parseDouble(discountInput);

            // SQL Query
            String sql = "INSERT INTO menuitemsadmin (name, description, category, size, base_price, sell_price, discount) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setString(3, category);
                pstmt.setString(4, size.isEmpty() ? null : size);
                pstmt.setDouble(5, basePrice);
                pstmt.setDouble(6, sellPrice);
                pstmt.setDouble(7, discount);
                pstmt.executeUpdate();
                System.out.println("Menu item added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewMenuItemsByCategorySeparately() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT category FROM menuitemsadmin ORDER BY category")) {

            while (rs.next()) {
                String category = rs.getString("category");
                System.out.println("\n--- " + category + " ---");
                displayItemsByCategory(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayItemsByCategory(String category) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM menuitemsadmin WHERE category = ? ORDER BY name";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, category);
                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("\n--- " + category + " ---");
                    Table table = new Table(5, BorderStyle.UNICODE_BOX_WIDE, ShownBorders.ALL);
                    table.addCell("ID", new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell("Name", new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell("Category", new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell("Price", new CellStyle(CellStyle.HorizontalAlign.center));
                    table.addCell("Discount", new CellStyle(CellStyle.HorizontalAlign.center));

                    while (rs.next()) {
                        table.addCell(String.valueOf(rs.getInt("item_id")), new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell(rs.getString("name"), new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell(rs.getString("category"), new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell(String.format("$%.2f", rs.getDouble("sell_price")), new CellStyle(CellStyle.HorizontalAlign.center));
                        table.addCell(String.format("$%.2f", rs.getDouble("discount")), new CellStyle(CellStyle.HorizontalAlign.center));
                    }
                    System.out.println(table.render());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<Map<String, Object>> getAllMenuItems() {
        List<Map<String, Object>> menuItems = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM menu_items";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("item_id", rs.getInt("item_id"));
                    item.put("name", rs.getString("name"));
                    item.put("description", rs.getString("description"));
                    item.put("category", rs.getString("category"));
                    item.put("size", rs.getString("size"));
                    item.put("base_price", rs.getDouble("base_price"));
                    item.put("sell_price", rs.getDouble("sell_price"));
                    item.put("discount", rs.getDouble("discount"));
                    menuItems.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return menuItems;
    }

    private static String getCategoryFromNumber(int categoryNumber) {
        switch (categoryNumber) {
            case 1:
                return "Appetizers";
            case 2:
                return "Main Course";
            case 3:
                return "Beverages";
            case 4:
                return "Desserts";
            default:
                return "Unknown";
        }
    }
}
