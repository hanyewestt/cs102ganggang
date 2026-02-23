
import java.util.*;

public class NobleTile {

    private HashMap<Gem, Integer> tokens = new HashMap<>(6);
    private static final int POINTS = 3;

    public NobleTile() {
        for (Gem g : Gem.values()) {
            this.tokens.put(g, 0);
        }
    }

    // public NobleTile() {

    // }

    public HashMap<Gem, Integer> getTokens() {
        return this.tokens;
    }

    public void setTokens(Gem g, int amount) {
        this.tokens.put(g, amount);
    }
}
