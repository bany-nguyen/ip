package bany.utilities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Validates required and optional tags in task-creation commands. */
public class CommandValidator {

    /** Creates a validator using Bany's built-in command rules. */
    public CommandValidator() {}

    /**
     * Finds a repeated tag that is required for the given task type.
     *
     * @param command task command being validated.
     * @param tagNames tag names in their input order.
     * @return the first duplicated critical tag, or {@code null} if none exists.
     */
    public String findDuplicateCriticalTag(String command, List<String> tagNames) {
        Set<String> criticalTags = switch (command) {
            case "DEADLINE" -> Set.of("by");
            case "EVENT" -> Set.of("from", "to");
            default -> Set.of();
        };

        Set<String> seenTags = new HashSet<>();
        for (String tagName : tagNames) {
            if (criticalTags.contains(tagName) && !seenTags.add(tagName)) {
                return tagName;
            }
        }
        return null;
    }

    /**
     * Checks whether tags are extra, missing, duplicated, or out of order.
     *
     * @param command task command being validated.
     * @param tagNames tag names in their input order.
     * @return {@code true} if a warning should be shown.
     */
    public boolean hasTagWarning(String command, List<String> tagNames) {
        List<String> expectedOrder = switch (command) {
            case "DEADLINE" -> List.of("by");
            case "EVENT" -> List.of("from", "to");
            default -> List.of();
        };

        Set<String> expectedTags = Set.copyOf(expectedOrder);
        boolean hasExtraTag = tagNames.stream()
                .anyMatch(tagName -> !expectedTags.contains(tagName));
        List<String> actualRequiredOrder = tagNames.stream()
                .filter(expectedTags::contains)
                .toList();
        boolean hasWrongOrder = !actualRequiredOrder.equals(expectedOrder);

        return hasExtraTag || hasWrongOrder;
    }

}
