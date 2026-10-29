package Utilities;

// Milestone 4 - Nguyen Tri Dung - Console Input Utilities
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class DataInput {
    private static final Scanner scanner = new Scanner(System.in);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String getString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static String getNonEmptyString(String prompt) {
        String input;
        while (true) {
            input = getString(prompt);
            if (!DataValidation.checkStringEmpty(input)) {
                return input;
            }
            System.out.println(">> Error: Data cannot be empty. Please enter again!");
        }
    }

    public static int getIntegerNumber(String prompt) {
        while (true) {
            try {
                String input = getString(prompt);
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Please enter a valid integer!");
            }
        }
    }

    public static int getIntegerNumber(String prompt, int min, int max) {
        while (true) {
            int number = getIntegerNumber(prompt);
            if (DataValidation.checkNumberInMinMax(number, min, max)) {
                return number;
            }
            System.out.printf(">> Error: Value must be in range [%d - %d]!\n", min, max);
        }
    }

    public static double getDoubleNumber(String prompt) {
        while (true) {
            try {
                String input = getString(prompt);
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Please enter a valid real number!");
            }
        }
    }

    public static double getPositiveDouble(String prompt) {
        while (true) {
            double number = getDoubleNumber(prompt);
            if (number > 0) {
                return number;
            }
            System.out.println(">> Error: Value must be greater than 0!");
        }
    }

    public static LocalDate getDate(String prompt) {
        while (true) {
            String input = getString(prompt + " (dd/MM/yyyy): ");
            if (input.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println(">> Error: Date is not in valid format dd/MM/yyyy!");
            }
        }
    }

    public static boolean getBoolean(String prompt) {
        while (true) {
            String input = getString(prompt + " (Y/N): ").toUpperCase();
            if (input.equals("Y") || input.equals("YES")) {
                return true;
            }
            if (input.equals("N") || input.equals("NO")) {
                return false;
            }
            System.out.println(">> Error: Please choose only Y or N!");
        }
    }
}
