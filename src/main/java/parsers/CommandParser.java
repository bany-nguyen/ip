package parsers;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.List;

/**
 * Parses chatbot commands into named values.
 *
 * <p>The parser expects input in this form:
 * {@code <command> <description> /<tag> <tag description> ...}.
 * The returned map contains lowercase keys. The value for the
 * {@code command} key is uppercase, while descriptions and tag values
 * retain the user's original casing.</p>
 */
public class CommandParser {
    /**
     * Splits a command into its command name, description, and tagged values.
     *
     * <p>The first word is always treated as the command. Everything after
     * that word and before the first slash is treated as the description.
     * The first slash and each later slash start a new tag. Each tag value
     * continues until the next slash or the end of input.
     * If a tag is repeated, the last value replaces the earlier value in the
     * returned map. Use {@link #getTagNames(String)} when repeated tags must
     * be detected.</p>
     *
     * @param input complete command entered by the user
     * @return an insertion-ordered map of keys to values
     * @throws IllegalArgumentException if {@code input} is null or blank
     */
    public static Map<String, String> parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command cannot be null or blank");
        }

        String trimmedInput = input.trim();
        int commandEnd = firstWhitespaceIndex(trimmedInput);
        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        if (commandEnd == -1) {
            result.put("command", trimmedInput.toUpperCase(Locale.ROOT));
            result.put("description", "");
            return result;
        }

        result.put("command", trimmedInput.substring(0, commandEnd)
                .toUpperCase(Locale.ROOT));
        String arguments = trimmedInput.substring(commandEnd).trim();
        int firstTag = findTagStart(arguments, 0);

        if (firstTag == -1) {
            result.put("description", arguments);
            return result;
        }

        result.put("description", arguments.substring(0, firstTag).trim());
        parseTags(arguments, firstTag, result);
        return result;
    }

    /**
     * Returns the tag names in the order in which they appear in the input.
     * Repeated tag names are retained in the returned list.
     *
     * @param input complete command entered by the user
     * @return lowercase tag names in input order
     * @throws IllegalArgumentException if {@code input} is null, blank, or malformed
     */
    public static List<String> getTagNames(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command cannot be null or blank");
        }

        String trimmedInput = input.trim();
        int commandEnd = firstWhitespaceIndex(trimmedInput);
        if (commandEnd == -1) {
            return List.of();
        }

        String arguments = trimmedInput.substring(commandEnd).trim();
        int tagStart = findTagStart(arguments, 0);
        if (tagStart == -1) {
            return List.of();
        }

        List<String> tagNames = new ArrayList<>();
        while (tagStart < arguments.length()) {
            int tagNameStart = tagStart + 1;
            int tagNameEnd = tagNameStart;

            while (tagNameEnd < arguments.length()
                    && !Character.isWhitespace(arguments.charAt(tagNameEnd))
                    && arguments.charAt(tagNameEnd) != '/') {
                tagNameEnd++;
            }

            if (tagNameEnd == tagNameStart) {
                throw new IllegalArgumentException("Tag name cannot be empty");
            }

            tagNames.add(arguments.substring(tagNameStart, tagNameEnd)
                    .toLowerCase(Locale.ROOT));

            int valueStart = tagNameEnd;
            while (valueStart < arguments.length()
                    && Character.isWhitespace(arguments.charAt(valueStart))) {
                valueStart++;
            }

            int nextTag = findTagStart(arguments, valueStart);
            if (nextTag == -1) {
                return tagNames;
            }
            tagStart = nextTag;
        }
        return tagNames;
    }

    private static void parseTags(String arguments, int firstTag,
            Map<String, String> result) {
        int tagStart = firstTag;
        while (tagStart < arguments.length()) {
            int tagNameStart = tagStart + 1;
            int tagNameEnd = tagNameStart;

            while (tagNameEnd < arguments.length()
                    && !Character.isWhitespace(arguments.charAt(tagNameEnd))
                    && arguments.charAt(tagNameEnd) != '/') {
                tagNameEnd++;
            }

            if (tagNameEnd == tagNameStart) {
                throw new IllegalArgumentException("Tag name cannot be empty");
            }

            String tagName = arguments.substring(tagNameStart, tagNameEnd)
                    .toLowerCase(Locale.ROOT);
            int valueStart = tagNameEnd;
            while (valueStart < arguments.length()
                    && Character.isWhitespace(arguments.charAt(valueStart))) {
                valueStart++;
            }

            int nextTag = findTagStart(arguments, valueStart);
            int valueEnd = nextTag == -1 ? arguments.length() : nextTag;
            String tagValue = arguments.substring(valueStart, valueEnd).trim();
            result.put(tagName, tagValue);

            if (nextTag == -1) {
                return;
            }
            tagStart = nextTag;
        }
    }

    private static int firstWhitespaceIndex(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isWhitespace(input.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int findTagStart(String input, int fromIndex) {
        return input.indexOf('/', fromIndex);
    }

}
