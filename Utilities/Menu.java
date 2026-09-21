package Utilities;

import java.util.Arrays;
import java.util.List;

public class Menu {

    private Menu() {
        // Utility class, không khởi tạo đối tượng
    }

    public static void print(String menuString) {
        List<String> menuItems = Arrays.asList(menuString.split("\\|"));
        for (String item : menuItems) {
            if (item.trim().equalsIgnoreCase("Select:") || item.trim().equalsIgnoreCase("Lựa chọn:")) {
                System.out.print(item.trim() + " ");
            } else {
                System.out.println(item.trim());
            }
        }
    }

    public static int getUserChoice() {
        return DataInput.getIntegerNumber("");
    }

    public static void printHeader(String title) {
        int width = 60;
        String border = "=".repeat(width);
        System.out.println("\n" + border);
        int padding = (width - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(0, padding)) + title.toUpperCase());
        System.out.println(border);
    }
}
