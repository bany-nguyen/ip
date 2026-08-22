package utilities;

import java.util.List;
import java.util.Set;

/**
 * Stores the commands recognised by Bany and provides command validation.
 */
public final class CommandStorage {
    private static final Set<String> VALID_TASK_COMMANDS = Set.of(
            "TODO", "DEADLINE", "EVENT");

    private static final Set<String> VALID_ACTION_COMMANDS = Set.of(
            "BYE", "LIST", "MARK", "UNMARK", "DELETE", "FIND");

    private static final List<String> ALL_COMMANDS = List.of(
            "TODO", "DEADLINE", "EVENT",
            "BYE", "LIST", "MARK", "UNMARK", "DELETE", "FIND");

    private CommandStorage() {
        // Prevent this utility class from being instantiated.
    }

    /**
     * Checks whether a word is a task command.
     *
     * @param word command word to check
     * @return true if the word creates a task
     */
    public static boolean checkValidTaskWord(String word) {
        return VALID_TASK_COMMANDS.contains(word);
    }

    /**
     * Checks whether a word is an action command.
     *
     * @param word command word to check
     * @return true if the word performs an action
     */
    public static boolean checkValidActionWord(String word) {
        return VALID_ACTION_COMMANDS.contains(word);
    }

    /**
     * Checks whether a word is any recognised Bany command.
     *
     * @param word command word to check
     * @return true if the word is recognised
     */
    public static boolean checkValidAllCommandWord(String word) {
        return checkValidTaskWord(word) || checkValidActionWord(word);
    }

    /**
     * Returns all commands in a stable order for command suggestions.
     *
     * @return all commands accepted by Bany
     */
    public static List<String> getAllCommands() {
        return ALL_COMMANDS;
    }
}
