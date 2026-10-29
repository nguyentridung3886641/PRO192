package Utilities;

// Milestone 4 - Nguyen Tri Dung - Menu & UI Chart Utilities
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Menu {

    private Menu() {
    }

    public static void print(String menuString) {
        if (menuString == null) return;
        Arrays.stream(menuString.split("\\|"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .forEach(System.out::println);
    }

    public static String repeat(String str, int count) {
        if (str == null || count <= 0) return "";
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static void printAsciiBarChart(String chartTitle, List<String> labels, List<Integer> values, String unit) {
        System.out.println("\n  +----------------------------------------------------------------------------------+");
        System.out.printf("  | VISUAL BAR CHART: %-62s |\n", chartTitle);
        System.out.println("  +----------------------------------------------------------------------------------+");

        if (labels == null || values == null || labels.isEmpty() || labels.size() != values.size()) {
            System.out.println("  |   (No data available to display chart)                                           |");
            System.out.println("  +----------------------------------------------------------------------------------+");
            return;
        }

        int maxVal = values.stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxBarLength = 35;

        IntStream.range(0, labels.size()).forEach(i -> {
            String label = labels.get(i);
            if (label.length() > 26) {
                label = label.substring(0, 23) + "..."; //In nhãn sách -> độ dài lớn hơn 26 thì biểu thị ...
            }
            int val = values.get(i);
            int barLength = 0;
            if (maxVal > 0) {
                barLength = (int) Math.round(((double) val / maxVal) * maxBarLength);
                if (val > 0 && barLength == 0) {
                    barLength = 1;
                }
            }

            String bar = repeat("=", barLength);
            System.out.printf("  | %-26s | %-35s %4d %-10s |\n",
                    label, bar, val, unit);
        });
        System.out.println("  +----------------------------------------------------------------------------------+");
    }
}
