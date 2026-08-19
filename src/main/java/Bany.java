import java.util.Scanner;

public class Bany {
    public static void main(String[] args) {
        String banner = """
                ____________________________________________________________
                 ____                      
                | __ )  __ _ _ __  _   _ 
                |  _ \\ / _` | '_ \\| | | |
                | |_) | (_| | | | | |_| |
                |____/ \\__,_|_| |_|\\__, |
                                   |___/ 
                Hello! I'm Bany.
                What can I do for you?
                ____________________________________________________________""";
        System.out.println(banner);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            System.out.println("____________________________________________________________");
            if (line.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            }
            System.out.println(line);
            System.out.println("____________________________________________________________");
        }
    }
}
