package item;

import java.util.*;
import util.Utility;

public class NobleTile {

    private HashMap<Gem, Integer> tokens = Utility.generateEmptyHashmap();
    private static final int POINTS = 3;

    public NobleTile(Gem gem1, Gem gem2, Gem gem3) {
        tokens.replace(gem1, 3);
        tokens.replace(gem2, 3);
        tokens.replace(gem3, 3);
    }

    public NobleTile(Gem gem1, Gem gem2) {
        tokens.replace(gem1, 4);
        tokens.replace(gem2, 4);
    }

    public HashMap<Gem, Integer> getTokens() {
        return tokens;
    }

    public int getPoints() {
        return POINTS;
    }
}
