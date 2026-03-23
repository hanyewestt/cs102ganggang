package item;

import java.util.*;
import util.Utility;

public class NobleTile {

    private HashMap<Gem, Integer> tokens = Utility.generateEmptyHashmap();
    private static final int POINTS = 3;

    /**
     * Constructor for NobelTile that initialises the Gem Hashmap
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
     * Constructor for NobelTile that initialises the Gem Hashmap
     * 
     * @param gem1 GemType
     * @param gem2 GemType
     */
    public NobleTile(Gem gem1, Gem gem2) {
        tokens.replace(gem1, 4);
        tokens.replace(gem2, 4);
    }

    /**
     * @return Cost of NobelTile as HashMap
     */
    public HashMap<Gem, Integer> getTokens() {
        return tokens;
    }

    /**
     * @return Prestige points of Card
     */
    public int getPoints() {
        return POINTS;
    }

    /**
     * @return NobelTile info to be displayed on console
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ").append(POINTS).append(" | ");
        
        Iterator tokenIterator = tokens.entrySet().iterator();
        boolean first = true;
        while (tokenIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) tokenIterator.next();

            if ((int) entry.getValue() > 0) {
                if (first) {
                    sb.append(entry.getValue()).append(Utility.fromGemToChar((Gem)entry.getKey()));
                    first = false;
                } else {
                    sb.append(", ").append(entry.getValue()).append(Utility.fromGemToChar((Gem)entry.getKey()));
                }
            }
        }
        sb.append(" ]");

        return sb.toString();
    }

}
