package item;

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
        int gemWidth = 12;

        switch (Utility.fromGemToChar(GEMTYPE)) {
            case 'D':
                sb.append(String.format("%-" + gemWidth + "s", "Diamond"));
                break;
            case 'R':
                sb.append(String.format("%-" + gemWidth + "s", "Ruby"));
                break;
            case 'S':
                sb.append(String.format("%-" + gemWidth + "s", "Sapphire"));
                break;
            case 'E':
                sb.append(String.format("%-" + gemWidth + "s", "Emerald"));
                break;
            case 'O':
                sb.append(String.format("%-" + gemWidth + "s", "Onyx"));
                break;
            default:
                break;
        }
        
        sb.append(" | ");
        int pointWidth = 8;
        String pointsDisplay = String.format("%-" + pointWidth + "s", POINTS);
        sb.append(pointsDisplay).append(" | ");
        
        Iterator tokenIterator = tokens.entrySet().iterator();
        boolean first = true;
        int costWidth = 18;
        String costDisplay = "";
        while (tokenIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) tokenIterator.next();

            if ((int) entry.getValue() > 0) {
                if (first) {
                    first = false;
                } else {
                    costDisplay += ", ";
                }
                
                costDisplay += "" + entry.getValue() + Utility.fromGemToChar((Gem)entry.getKey());
                
            }
        }
        sb.append(String.format("%-" + costWidth + "s", costDisplay));
        sb.append(" ]");

        return sb.toString();
    }
}
