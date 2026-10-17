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

    /**
     * [LAMBDA & STREAM API]: Hiển thị chuỗi menu phân cách bởi ký tự '|'.
     * Áp dụng Arrays.stream, map(String::trim) và forEach Lambda.
     */
    public static void print(String menuString) {
        if (menuString == null) return;
        Arrays.stream(menuString.split("\\|"))
                .map(String::trim)
                .forEach(item -> {
                    if (item.equalsIgnoreCase("Select:") || item.equalsIgnoreCase("Lựa chọn:")) {
                        System.out.print(item + " ");
                    } else {
                        System.out.println(item);
                    }
                });
    }

    public static int getUserChoice() {
        return DataInput.getIntegerNumber("");
    }

    public static void printHeader(String title) {
        int width = 60;
        String border = "=".repeat(width);

        System.out.println("\n" + border);
        int padding = Math.max(0, (width - title.length()) / 2);
        System.out.println(" ".repeat(padding) + title.toUpperCase());
        System.out.println(border);
    }

    /**
     * [LAMBDA & STREAM API]: Tính năng 8 - Vẽ biểu đồ cột ngang bằng ký tự ASCII trên Console.
     * 
     * So sánh:
     * - Cách cũ: Dùng vòng for tìm maxVal và vòng for thứ 2 để in từng thanh.
     * - Lambda/Stream:
     *   1. Tìm giá trị lớn nhất: `values.stream().mapToInt(Integer::intValue).max().orElse(0)`
     *   2. Duyệt song song nhãn và giá trị: `IntStream.range(0, labels.size()).forEach(i -> ...)`
     *   3. Sinh chuỗi ký tự thanh bar: `"█".repeat(barLength)` gọn gàng và hiệu quả.
     */
    public static void printAsciiBarChart(String chartTitle, List<String> labels, List<Integer> values, String unit) {
        System.out.println("\n  ┌──────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("  │ 📊 VISUAL BAR CHART: %-59s │\n", chartTitle);
        System.out.println("  ├──────────────────────────────────────────────────────────────────────────────────┤");

        if (labels == null || values == null || labels.isEmpty() || labels.size() != values.size()) {
            System.out.println("  │   (No data available to display chart)                                           │");
            System.out.println("  └──────────────────────────────────────────────────────────────────────────────────┘");
            return;
        }

        // Bước 1: Tìm giá trị lớn nhất (max) bằng Stream API IntStream max()
        int maxVal = values.stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxBarLength = 35; // Độ dài tối đa của thanh biểu đồ (35 ký tự █)

        // Bước 2: Duyệt từng phần tử và vẽ thanh bar bằng IntStream.range và biểu thức Lambda
        java.util.stream.IntStream.range(0, labels.size()).forEach(i -> {
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

            String bar = "█".repeat(barLength);
            System.out.printf("  │ %-26s │ %-35s %4d %-10s │\n",
                    label, bar, val, unit);
        });
        System.out.println("  └──────────────────────────────────────────────────────────────────────────────────┘");
    }
}
