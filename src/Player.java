
import java.awt.geom.GeneralPath;
import java.util.*;
// Gem, Card, NobleTile

public class Player {

    private HashMap<Gem, Integer> tokens = new HashMap<>();

    private List<Card> reserveCards;
    private static final int RESERVE_HAND_SIZE = 3;

    private HashMap<Gem, Integer> production = new HashMap<>();
    private List<NobleTile> ownedNobles;

    private int points;

    public Player() {
        points = 0;

        for (Gem g : Gem.values()) {
            tokens.put(g, 0);
            production.put(g, 0);
        }

    }

    public int getPoints() {
        return points;
    }

    public int getReserveHandSize() {
        return reserveCards.size();
    }

    public List<Card> getReserveHand() {
        return reserveCards;
    }

    // display reserve hand?
    public HashMap<Gem, Integer> getTokens() {
        return tokens;
    }

    // display token?
    public HashMap<Gem, Integer> getProduction() {
        return production;
    }

    // display production
    public List<NobleTile> getOwnedNobleTile() {
        return ownedNobles;
    }

    // display noble tile
    public void addToken(Gem g, int amt) {
        tokens.put(g, tokens.get(g) + amt);
    }

    public void addProduction(Gem g) {
        production.put(g, production.get(g) + 1);
    }

    public void addPoints(int p) {
        points += p;
    }

    public boolean reserveCard(Card c) {
        if (getReserveHandSize() == RESERVE_HAND_SIZE) {
            return false;
        }
        reserveCards.add(c);
        return true;
    }

    // for gold
    // opt 1. user inputs n gold coins only.
    // opt 2. user has to input all the gems.
    // public boolean canBuy(Card c) {
    //     // todo 
    // }
    // public boolean canBuy(Card c, HashMap<Gem, Integer> selectedTokens) {
    //     // todo 
    // }
    // public void buyCard(Card c) {
    //     // todo
    //     if (canBuy(c)) {
    //         // deduct cost from tokens
    //         addToken(g, -i); 
    //         addProduction(gem);
    //         addPoints(cardPoints);
    //     }
    // }
    // public void buyCard(Card c, HashMap<Gem, Integer> selectedTokens) {
    //     // todo
    //     if (canBuy(c, selectedTokens)) {
    //         // deduct cost from tokens
    //         addToken(g, -i); // need to iterate through all the gem types, and also check for user input
    //         addProduction(gem);
    //         addPoints(cardPoints);
    //     }
    // }
}
