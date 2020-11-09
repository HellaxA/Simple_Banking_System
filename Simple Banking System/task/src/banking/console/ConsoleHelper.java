package banking.console;

import java.util.Scanner;

public class ConsoleHelper {

    private static Scanner scanner = new Scanner(System.in);

    public static void write(String value) {
        System.out.println(value);
    }

    public static String read() {
        return scanner.nextLine();
    }
}
