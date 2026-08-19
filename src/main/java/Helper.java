import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static int firstNumber(String input) {
        Matcher matcher = Pattern.compile("\\d+").matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return -1;
    }

    /**
     * Prints the divider used to separate chatbot messages.
     */
    public static void printDivider() {
        System.out.println("____________________________________________________________");
    }
}
