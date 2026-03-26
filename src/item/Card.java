package item;

import display.Display;
import java.util.*;
import util.*;

public class Card {

    private HashMap<Gem, Integer> tokens = Utility.generateEmptyHashmap();
    private final Gem GEMTYPE;
    private final int POINTS;
    
    /**
     * Constructor for Card that initialises the Gem Hashmap
     * 
     * @param GEMTYPE Gem enum
     * @param POINTS points of Card
     * @param Diamond no. of Diamonds needed to purchase Card
     * @param Ruby no. of Ruby needed to purchase Card
     * @param Sapphire no. of Sapphire needed to purchase Card
     * @param Emerald no. of Emerald needed to purchase Card
     * @param Onyx no. of Onyx needed to purchase Card
     */
    public Card(Gem GEMTYPE, int POINTS, int Diamond, int Ruby, int Sapphire, int Emerald, int Onyx) {
        this.GEMTYPE = GEMTYPE;
        this.POINTS = POINTS;
        tokens.put(Gem.Diamond, Diamond);
        tokens.put(Gem.Ruby, Ruby);
        tokens.put(Gem.Sapphire, Sapphire);
        tokens.put(Gem.Emerald, Emerald);
        tokens.put(Gem.Onyx, Onyx);
        tokens.put(Gem.Gold, 0);
    }

    /**
     * @return Cost of Card as HashMap
     */
    public HashMap<Gem, Integer> getTokens() {
        return tokens;
    }

    /**
     * @return Gem production type of Card
     */
    public Gem getGemType() {
        return GEMTYPE;
    }

    /**
     * @return Prestige points of Card
     */
    public int getPoints() {
        return POINTS;
    }

    /**
     * @return Card info to be displayed on console
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");

        // print gems
        int gemWidth = 8;
        sb.append(String.format("%-" + gemWidth + "s", GEMTYPE));
        sb.append(" | ");

        // print points
        int pointWidth = 8;
        sb.append(String.format("%-" + pointWidth + "s", POINTS));
        sb.append(" | ");
        
        // print costs
        int costWidth = 14;

        sb.append(String.format("%-" + costWidth + "s", Display.costDisplayString(tokens)));
        sb.append(" ]");

        return sb.toString();
    }
}
