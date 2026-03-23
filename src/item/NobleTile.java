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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ").append(POINTS).append(" | ");

        Iterator tokenIterator = tokens.entrySet().iterator();
        boolean first = true;
        while (tokenIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) tokenIterator.next();

            if ((int) entry.getValue() > 0) {
                if (first) {
                    sb.append(entry.getValue()).append(Utility.fromGemToChar((Gem) entry.getKey()));
                    first = false;
                } else {
                    sb.append(", ").append(entry.getValue()).append(Utility.fromGemToChar((Gem) entry.getKey()));
                }
            }
        }
        sb.append(" ]");

        return sb.toString();
    }

}
