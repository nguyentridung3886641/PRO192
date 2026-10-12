package Utilities;

import java.util.Arrays;
import java.util.List;

/**
 * Lớp tiện ích Menu hỗ trợ hiển thị giao diện điều khiển và vẽ biểu đồ cột ASCII trực quan.
 * PRO192 - Tầng Tiện ích & Giao diện (Member 4)
 */
public class Menu {

    private Menu() {
    }

    public static void print(String menuString) {
        List<String> menuItems = Arrays.asList(menuString.split("\\|"));
        for (int i = 0; i < menuItems.size(); i++) {
            String item = menuItems.get(i);
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
        StringBuilder borderBuilder = new StringBuilder();
        for (int i = 0; i < width; i++) {
            borderBuilder.append("=");
        }
        String border = borderBuilder.toString();

        System.out.println("\n" + border);
        int padding = (width - title.length()) / 2;
        StringBuilder padBuilder = new StringBuilder();
        for (int i = 0; i < Math.max(0, padding); i++) {
            padBuilder.append(" ");
        }
        System.out.println(padBuilder.toString() + title.toUpperCase());
        System.out.println(border);
    }

    /**
     * Tính năng 8: Vẽ biểu đồ cột ngang bằng ký tự ASCII ngay trên Console.
     * Viết bằng vòng lặp for truyền thống, trực quan và dễ hiểu.
     *
     * @param chartTitle Tiêu đề biểu đồ
     * @param labels     Danh sách nhãn của từng cột
     * @param values     Danh sách giá trị số nguyên tương ứng
     * @param unit       Đơn vị tính (VD: "cuốn", "lượt", "độc giả")
     */
    public static void printAsciiBarChart(String chartTitle, List<String> labels, List<Integer> values, String unit) {
        System.out.println("\n  ┌──────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("  │ 📊 BIỂU ĐỒ TRỰC QUAN: %-58s │\n", chartTitle);
        System.out.println("  ├──────────────────────────────────────────────────────────────────────────────────┤");

        if (labels == null || values == null || labels.isEmpty() || labels.size() != values.size()) {
            System.out.println("  │   (Chưa có dữ liệu để hiển thị biểu đồ)                                          │");
            System.out.println("  └──────────────────────────────────────────────────────────────────────────────────┘");
            return;
        }

        // Bước 1: Tìm giá trị lớn nhất (max) bằng vòng lặp for truyền thống để tính tỉ lệ cột
        int maxVal = 0;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) > maxVal) {
                maxVal = values.get(i);
            }
        }

        int maxBarLength = 35; // Độ dài tối đa của thanh biểu đồ (35 ký tự █)

        // Bước 2: Duyệt qua từng phần tử và vẽ thanh bar bằng vòng lặp for
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            if (label.length() > 26) {
                label = label.substring(0, 23) + "...";
            }
            int val = values.get(i);
            int barLength = 0;
            if (maxVal > 0) {
                barLength = (int) Math.round(((double) val / maxVal) * maxBarLength);
                if (val > 0 && barLength == 0) {
                    barLength = 1;
                }
            }

            StringBuilder bar = new StringBuilder();
            for (int j = 0; j < barLength; j++) {
                bar.append("█");
            }

            System.out.printf("  │ %-26s │ %-35s %4d %-10s │\n",
                    label, bar.toString(), val, unit);
        }
        System.out.println("  └──────────────────────────────────────────────────────────────────────────────────┘");
    }
}
