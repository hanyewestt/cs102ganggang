import java.util.HashMap;

public class Card {
    HashMap<String, Integer> tokens = new HashMap<>();
    private final char GEMTYPE; 
    private final int POINTS; 

    public Card(char GEMTYPE, int POINTS, int Diamond, int Ruby, int Sapphire, int Emerald, int Onyx) {
        this.GEMTYPE = GEMTYPE;
        this.POINTS = POINTS;
        tokens.put("Diamond", Diamond);
        tokens.put("Ruby", Ruby);
        tokens.put("Sapphire", Sapphire);
        tokens.put("Emerald", Emerald);
        tokens.put("Onyx", Onyx);
    }

    public HashMap<String, Integer> getTokens() {
        return tokens;
    }

    public char getGEMTYPE() {
        return GEMTYPE;
    }

    public int getPOINTS() {
        return POINTS;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Card{");
        sb.append("tokens=").append(tokens);
        sb.append(", GEMTYPE=").append(GEMTYPE);
        sb.append(", POINTS=").append(POINTS);
        sb.append('}');
        return sb.toString();
    }
}