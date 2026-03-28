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
     * Creates a deep copy of an existing HashMap.
     *
     * @param toCopy The HashMap to copy.
     *
     * @return The HashMap that is being returned.
     */
    public static HashMap<Gem, Integer> generateHashMapClone(HashMap<Gem, Integer> toCopy) {
        HashMap<Gem, Integer> copy = new HashMap<>();
        for (Gem g : Gem.values()) {
            copy.put(g, toCopy.get(g));
        }

        return copy;
    }

    public static Card[][] generateMarketClone(Card[][] market) {
        Card[][] copy = new Card[3][4];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                copy[i][j] = market[i][j];
            }
        }

        return copy;
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
     * Performs subtraction with gold on two HashMaps with Gem, Integer. Returns
     * the amount of tokens that are subtracted, with gold as a wildcard.
     * Returns null if insufficient amount of tokens.
     *
     * @param tokens HashMap representing amount of tokens held.
     * @param cost HashMap representing cost, usually cards.
     * @return The subtraction amount.
     */
    public static HashMap<Gem, Integer> findSubtractionAmount(HashMap<Gem, Integer> tokens, HashMap<Gem, Integer> cost) {
        HashMap<Gem, Integer> result = generateEmptyHashmap();

        int goldAvailable = tokens.get(Gem.Gold);
        for (Gem g : Gem.values()) {
            if (g == Gem.Gold) {
                continue;
            }

            int difference = tokens.get(g) - cost.get(g);
            if (difference < 0) {
                difference = Math.abs(difference);
                if (goldAvailable < difference) {
                    return null;
                }

                goldAvailable -= difference;
                result.replace(Gem.Gold, result.get(Gem.Gold) + difference);
                result.replace(g, tokens.get(g));
            } else {
                result.replace(g, cost.get(g));
            }
        }

        return result;
    }

    /**
     * Subtracts the number of gems in the first HashMap by the amount of said
     * gem in right HashMap. Use findSubtractionAmount to find the second
     * HashMap and ensure it is not null or more than the first.
     *
     * @param orig The HashMap to be modified.
     * @param subtractAmount The HashMap containing the amount of gems to
     * subtract.
     */
    public static void subtract(HashMap<Gem, Integer> orig, HashMap<Gem, Integer> subtractAmount) {
        for (Gem g : Gem.values()) {
            orig.replace(g, orig.get(g) - subtractAmount.get(g));
        }
    }

    /**
     * Subtracts the number of gems in the first HashMap by the amount of said
     * gem in right HashMap, ignoring Gold, with a minimum value of 0 left.
     * Ensure both HashMaps are not null beforehand.
     *
     * @param orig The HashMap to be modified.
     * @param discountAmount The HashMap containing the amount of gems to
     * discount.
     */
    public static void discount(HashMap<Gem, Integer> orig, HashMap<Gem, Integer> discountAmount) {
        for (Gem g : Gem.values()) {
            if (g == Gem.Gold) {
                continue;
            }

            int afterDiscount = orig.get(g) - discountAmount.get(g);
            orig.replace(g, afterDiscount < 0 ? 0 : afterDiscount);
        }
    }

    public static boolean isGreaterOrEqual(HashMap<Gem, Integer> first, HashMap<Gem, Integer> second) {
        for (Gem g : Gem.values()) {
            if (first.get(g) < second.get(g)) {
                return false;
            }
        }

        return true;
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

            System.out.println("‼️ Invalid input! Try again! ‼️");
            isValid = false;
        } while (!isValid);

        return false;
    }

    public static Gem askForGem(Scanner keyboard, String message) {
        return askForGem(keyboard, message, false);
    }

    /**
     * Will prompt the user for a string representing a Gem. Keeps prompting
     * until a valid input is given.
     *
     * @param keyboard The Scanner that is looking at keyboard input.
     * @param message The message to prompt for user input.
     * @return The Gem that the user inputs.
     */
    public static Gem askForGem(Scanner keyboard, String message, boolean takesGold) {
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
                case "cancel":
                    return null;
                case "gold":
                    if (takesGold) {
                        return Gem.Gold;
                    }
                default:
                    System.out.println("‼️ Invalid input! Try again! ‼️");
                    isValid = false;
            }
        } while (!isValid);

        return null;
    }

    /**
     * Will prompt the user for an positive integer with a min and max number
     * limitation. Keeps prompting until a valid input is given.
     *
     * @param keyboard The Scanner that is looking at keyboard input.
     * @param min The minimum integer allowed (includive).
     * @param max The maximum integer allowed (inclusive).
     * @param message The message to prompt for user input.
     * @return The integer that the user inputs.
     */
    public static int askForNum(Scanner keyboard, int min, int max, String message) {
        boolean isValid;

        do {
            System.out.print(message);

            try {
                int num = keyboard.nextInt();

                if (num >= min && num <= max) {
                    keyboard.nextLine();
                    return num;
                } else {
                    System.out.println("‼️ Out of bounds! Enter a number between " + min + " and " + max + " ‼️");
                }
            } catch (Exception e) {
                System.out.println("‼️ Invalid format for a number! Try again! ‼️");
            }

            keyboard.nextLine();

            isValid = false;
        } while (!isValid);

        return -1;
    }

    /**
     * Prompts the user for the row and column position of the card on the
     * board. User can enter '0' to cancel at any time.
     *
     * @return returns an array of size 2 {row, col}. Returns null if users
     * cancels.
     */
    public static int[] getPositionOnBoard(Scanner sc) {
        final int ROW_MAX = 3;
        final int COL_MAX = 4;

        String rowMessage = "Enter row number of your chosen card (1-3), 0 to cancel: ";
        int row = askForNum(sc, 0, 3, rowMessage);
        if (row == 0) {
            return null;
        }

        String colMessage = "Enter col number of your chosen card (1-4), 0 to cancel: ";
        int col = askForNum(sc, 0, 4, colMessage);
        if (col == 0) {
            return null;
        }

        int[] result = new int[2];
        result[0] = row - 1;
        result[1] = col - 1;

        return result;

    }

    /**
     * Converts a Gem to a char Returns X if Gem is invalid.
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
     * Converts a char to a Gem. Returns null if char is invalid.
     *
     * @param character The char to be read.
     * @return The Gem represented by that char.
     */
    public static Gem fromCharToGem(char character) {
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
