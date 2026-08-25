package bany.utilities;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * Provides small utility methods used by the Bany chatbot.
 */
public class Helper {

    /** Creates a helper utility object. */
    public Helper() {
    }

    /**
     * Prints the divider used to separate chatbot messages.
     */
    public static void printDivider() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Checks whether text can be parsed as a 32-bit signed integer.
     *
     * @param str text to check.
     * @return {@code true} if the text represents an integer.
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Finds the allowed command with the smallest Levenshtein distance from
     * the user's input.
     *
     * <p>Command matching is case-insensitive. If multiple commands have the
     * same distance, the first command in {@link CommandStorage#getAllCommands()}
     * is returned.</p>
     *
     * @param input command text entered by the user.
     * @return the closest allowed command, or {@code null} for blank input.
     */
    public static String closestWordMatch(String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }

        String normalizedInput = input.trim().toUpperCase(Locale.ROOT);
        String closestCommand = null;
        int smallestDistance = Integer.MAX_VALUE;

        for (String command : CommandStorage.getAllCommands()) {
            int distance = StringUtils.getLevenshteinDistance(normalizedInput, command);
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestCommand = command;
            }
        }

        return closestCommand;
    }
}
