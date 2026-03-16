package util;

import item.*;
import java.util.*;

public class Utility {

    /**
     * Returns an empty hashmap with Gem keys, and their respective values
     * initialised to 0.
     *
     * @return The empty hashmap.
     */
    public static HashMap<Gem, Integer> generateEmptyHashmap() {
        HashMap<Gem, Integer> result = new HashMap<>(Gem.values().length);
        for (Gem gem : Gem.values()) {
            result.put(gem, 0);
        }

        return result;
    }

    /**
     * Returns the total gems within a Hashmap.
     *
     * @param tokens The HashMap to be considered.
     * @return The total number of gems.
     */
    public static int getTotalGems(HashMap<Gem, Integer> tokens) {
        int sum = 0;
        for (Gem gem : Gem.values()) {
            sum += tokens.get(gem);
        }

        return sum;
    }

    /**
     * Will prompt the user for an input of 'Y' or 'N' for a decision. Keeps
     * prompting until a valid input is given.
     *
     * @param keyboard The Scanner that is looking at keyboard input.
     * @param message The message to prompt for user input.
     * @return The boolean representing their final decision.
     */
    public static boolean willProceed(Scanner keyboard, String message) {
        boolean isValid;
        do {
            System.out.print(message);
            String input = keyboard.nextLine().toLowerCase();

            if (input.compareTo("y") == 0) {
                return true;
            } else if (input.compareTo("n") == 0) {
                return false;
            }

            System.out.println("Invalid input! Try again!");
            isValid = false;
        } while (!isValid);

        return false;
    }

    /**
     * Will prompt the user for a string representing a Gem. Keeps prompting
     * until a valid input is given.
     *
     * @param keyboard The Scanner that is looking at keyboard input.
     * @param message The message to prompt for user input.
     * @return The Gem that the user inputs.
     */
    public static Gem askForGem(Scanner keyboard, String message) {
        boolean isValid;
        do {
            System.out.print(message);
            String input = keyboard.nextLine().toLowerCase();

            input = input.toLowerCase();

            switch (input) {
                case "diamond":
                    return Gem.Diamond;
                case "ruby":
                    return Gem.Ruby;
                case "sapphire":
                    return Gem.Sapphire;
                case "emerald":
                    return Gem.Emerald;
                case "onyx":
                    return Gem.Onyx;
                default:
                    System.out.println("Invalid input! Try again!");
                    isValid = false;
            }
        } while (!isValid);

        return null;
    }

    /**
     * Will prompt the user for an positive integer with a max number
     * limitation. Keeps prompting until a valid input is given.
     *
     * @param keyboard The Scanner that is looking at keyboard input.
     * @param max The maximum integer allowed (inclusive).
     * @param message The message to prompt for user input.
     * @return The integer that the user inputs.
     */
    public static int askForNum(Scanner keyboard, int max, String message) {
        boolean isValid;

        do {
            System.out.print(message);

            try {
                int num = keyboard.nextInt();

                if (num > 0 && num <= max) {
                    keyboard.nextLine();
                    return num;
                } else {
                    System.out.println("Out of bounds! Enter a number between 1 and " + max);
                }
            } catch (Exception e) {
                System.out.println("Invalid format for a number! Try again!");
            }

            keyboard.nextLine();

            isValid = false;
        } while (!isValid);

        return -1;
    }

    /**
     * Converts a Gem to a char
     * Returns X if Gem is invalid. 
     * 
     * @param Gem The Gem to be read.
     * @return The char representing that Gem.
     */
    public static char fromGemToChar(Gem gem) {
        switch (gem) {
            case Diamond:
                return 'D';
            case Ruby:
                return 'R';
            case Sapphire:
                return 'S';
            case Emerald:
                return 'E';
            case Onyx:
                return 'O';
            case Gold:
                return 'G';
            default:
                return 'X';
        }
    }

    /**
     * Converts a char to a Gem.
     * Returns null if char is invalid.
     * 
     * @param character The char to be read.
     * @return The Gem represented by that char.
     */
    private static Gem fromCharToGem(char character) {
        switch (character) {
            case 'D':
                return Gem.Diamond;
            case 'R':
                return Gem.Ruby;
            case 'S':
                return Gem.Sapphire;
            case 'E':
                return Gem.Emerald;
            case 'O':
                return Gem.Onyx;
            default:
                return null;
        }
    }
}