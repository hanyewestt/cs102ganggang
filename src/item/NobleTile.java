package item;

import java.util.*;

public class NobleTile {

    private HashMap<Gem, Integer> tokens = new HashMap<>(Gem.values().length);
    private static final int POINTS = 3;

    public NobleTile(Gem gem, int Diamond, int Ruby, int Sapphire, int Emerald, int Onyx) {
        tokens.put(Gem.Diamond, Diamond);
        tokens.put(Gem.Ruby, Ruby);
        tokens.put(Gem.Sapphire, Sapphire);
        tokens.put(Gem.Emerald, Emerald);
        tokens.put(Gem.Onyx, Onyx);
        tokens.put(Gem.Gold, 0);
    }

    public HashMap<Gem, Integer> getTokens() {
        return this.tokens;
    }

    public int getPoints() {
        return this.POINTS;
    }
}
