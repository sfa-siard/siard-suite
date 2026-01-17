/*
Copyright  : 2009, Enter AG, Zurich, Switzerland
             2024, Puzzle ITC GmbH, Switzerland
Created    : May 13, 2009, Hartwig Thomas
*/
package ch.enterag.utils.cli;

import java.io.File;
import java.util.*;

/**
 * This class parses the command line and makes the arguments
 * accessible as named options by name and unnamed arguments by
 * position.
 * In the name of simplicity this class does support switches that
 * indicate a boolean value just by their presence.
 *
 * @author Hartwig Thomas
 */
public class Arguments {
    /**
     * Container of named options.
     */
    private final Map<String, String> options = new HashMap<>();

    /**
     * Container of unnamed arguments.
     */
    private final String[] arguments;

    /**
     * Container of errors.
     */
    private final List<String> errors = new ArrayList<>();

    /**
     * Constructor parses the command-line arguments
     *
     * @param cliArguments command-line arguments.
     */
    public Arguments(String[] cliArguments) {
        this.arguments = Arrays.stream(cliArguments).filter(arg -> !isOption(arg)).toArray(String[]::new);
        Arrays.stream(cliArguments).filter(Arguments::isOption).forEach(this::parseArguments);
    }

    /**
     * Determines if the command-line argument string is an option.
     *
     * @param arg The command-line argument.
     * @return True if the argument is an option.
     */
    private static boolean isOption(String arg) {
        return arg.startsWith("-") || (!File.separator.equals("/") && arg.startsWith("/"));
    }

    /**
     * Get value of parsed option.
     *
     * @param name Name of option.
     * @return Option value (null for missing option, "" for missing value).
     */
    public String getOption(String name) {
        return options.get(name);
    }

    /**
     * Get value of unnamed argument.
     *
     * @param argPosition Position of argument (0 based).
     * @return Argument value.
     */
    public String getArgument(int argPosition) {
        return arguments[argPosition];
    }

    /**
     * @return Number of unnamed arguments.
     */
    public int getArguments() {
        return arguments.length;
    }

    /**
     * @return String of all parsing errors.
     */
    public String getErrors() {
        return String.join(", ", this.errors);
    }

    /**
     * Parses an argument to extract an option and its value.
     *
     * @param arg The command-line argument string to be parsed.
     */
    private void parseArguments(String arg) {
        int optPosition = getOptPosition(arg);
        if (optPosition > 1) {
            addArgumentsToOptions(arg, optPosition);
        } else
            errors.add("Empty option encountered!");
    }

    /**
     * Adds parsed option name and value to options map.
     *
     * @param arg The command-line argument string containing the option.
     * @param optPosition The position in the string where the option name ends.
     */
    private void addArgumentsToOptions(String arg, int optPosition) {
        String optionName = arg.substring(arg.startsWith("--") ? 2 : 1, optPosition);
        String optionValue = "";
        if (optPosition < arg.length()) {
            if ((arg.charAt(optPosition) == ':') ||
                    (arg.charAt(optPosition) == '='))
                optionValue = arg.substring(optPosition + 1);
            else
                errors.add("Option " + optionName + " must be terminated by colon, equals or blank!");
        }

        options.put(optionName, optionValue);
    }

    /**
     * Identifies the position in the command-line argument string where the option name ends.
     *
     * @param arg The command-line argument to analyse.
     * @return Index of the position where the option name ends.
     */
    private static int getOptPosition(String arg) {
        int optPosition = arg.startsWith("--") ? 2 : 1;
        while (optPosition < arg.length() && Character.isLetterOrDigit(arg.charAt(optPosition))) {
            optPosition++;
        }
        return optPosition;
    }

    /**
     * @param args Command-line arguments.
     * @return Arguments Instance.
     * @deprecated Use {@link #Arguments} to create instances.
     */
    @Deprecated
    public static Arguments getInstance(String[] args) {
        return new Arguments(args);
    }

}
