package item;

import display.Display;
import java.util.*;
import util.Utility;

/**
 * Represents a {@link NobleTile}. {@link NobleTile} contains HashMap of its
 * cost and the number prestige points awarded.
 */
public class NobleTile {

    private HashMap<Gem, Integer> tokens = Utility.generateEmptyHashmap();
    private static final int POINTS = 3;

    /**
     * Constructor for {@link NobleTile} that initialises the Gem Hashmap
     *
     * @param gem1 GemType
     * @param gem2 GemType
     * @param gem3 GemType
     */
    public NobleTile(Gem gem1, Gem gem2, Gem gem3) {
        tokens.replace(gem1, 3);
        tokens.replace(gem2, 3);
        tokens.replace(gem3, 3);
    }

    /**
     * Constructor for {@link NobleTile} that initialises the Gem Hashmap
     *
     * @param gem1 GemType
     * @param gem2 GemType
     */
    public NobleTile(Gem gem1, Gem gem2) {
        tokens.replace(gem1, 4);
        tokens.replace(gem2, 4);
    }

    /**
     * Get cost of the {@link NobleTile}.
     *
     * @return Cost of {@link NobleTile} as HashMap
     */
    public HashMap<Gem, Integer> getTokens() {
        return tokens;
    }

    /**
     * Gets prestige points that this {@link NobleTile} gives.
     *
     * @return Prestige points of {@link NobleTile}
     */
    public static int getPoints() {
        return POINTS;
    }

    /**
     * Prints {@link NobleTile} info to be displayed on console
     *
     * @return String info to be displayed on console
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("[ ");

        // print pts
        int pointWidth = 8;
        sb.append(String.format("%-" + pointWidth + "s", POINTS));
        sb.append(" | ");

        // print tokens
        int costWidth = 30 + 9 * (Display.costDisplayString(tokens).length() / 16);

        sb.append(String.format("%-" + costWidth + "s", Display.costDisplayString(tokens)));
        sb.append(" ]");

        return sb.toString();
    }

}
