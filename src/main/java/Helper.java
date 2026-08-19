import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides small utility methods used by the Bany chatbot.
 */
public class Helper {
    /**
     * Finds the first non-negative whole number in a command.
     *
     * @param input command text entered by the user
     * @return the first number found, or {@code -1} if no number is present
     */
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
