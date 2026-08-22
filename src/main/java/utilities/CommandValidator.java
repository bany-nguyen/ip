package utilities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandValidator {

    public CommandValidator() {}

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
