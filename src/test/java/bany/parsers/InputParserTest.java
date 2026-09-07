package bany.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/** Tests the command parser's handling of tag names and values. */
class InputParserTest {

    @Test
    void parseAndGetTagNames_repeatedTags_shareParsingRules() {
        String input = "event meeting /from 21-02-2026 21:03 "
                + "/to 22-02-2026 21:03 /to 23-02-2026 21:03";

        Map<String, String> values = InputParser.parse(input);

        assertEquals("23-02-2026 21:03", values.get("to"));
        assertEquals(List.of("from", "to", "to"), InputParser.getTagNames(input));
    }
}
