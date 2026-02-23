package item;

import java.util.*;
// Gem, Card, NobleTile

public class Player {

    private String name;

    private HashMap<Gem, Integer> tokens = new HashMap<>();

    private static final int RESERVE_HAND_SIZE = 3;
    private Card[] reserveCards = new Card[RESERVE_HAND_SIZE];

    private HashMap<Gem, Integer> production = new HashMap<>();
    private NobleTile[] ownedNobles = new NobleTile[5];

    private int points = 0;

    public Player() {

        for (Gem g : Gem.values()) {
            tokens.put(g, 0);
            production.put(g, 0);
        }

    }

    public Player(String name) {
        this();
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public int getReserveHandSize() {
        return reserveCards.length;
    }

    public Card[] getReserveHand() {
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
    public NobleTile[] getOwnedNobleTile() {
        return ownedNobles;
    }

    // display noble tile
    public void addToken(Gem g, int amt) {
        tokens.put(g, tokens.get(g) + amt);
    }

    public void addProduction(Gem g) {
        production.put(g, production.get(g) + 1);
    }

    private void addPoints(int p) {
        points += p;
    }

    public boolean reserveCard(Card c) {
        if (getReserveHandSize() == RESERVE_HAND_SIZE) {
            return false;
        }
        reserveCards[getReserveHandSize()] = c;

        return true;
    }

    // for gold
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
