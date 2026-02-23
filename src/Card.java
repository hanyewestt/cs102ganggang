
import java.util.HashMap;

public class Card {

    HashMap<Gem, Integer> tokens = new HashMap<>(6);
    private final Gem GEMTYPE;
    private final int POINTS;

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


    public HashMap<Gem, Integer> getTokens() {
        return tokens;
    }

    public Gem getGEMTYPE() {
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
