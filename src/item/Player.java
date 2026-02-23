package item;

import java.util.*;
import java.lang.*;
// Gem, Card, NobleTile

public class Player implements Comparable<Player>{

    private String name;

    private HashMap<Gem, Integer> tokens = new HashMap<>(Gem.values().length);

    private static final int RESERVE_HAND_SIZE = 3;
    private List<Card> reserveCards = new ArrayList<Card>(RESERVE_HAND_SIZE);

    private HashMap<Gem, Integer> production = new HashMap<>(Gem.values().length);
    private List<NobleTile> ownedNobles = new ArrayList<NobleTile>(5);

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

    public void removeToken(Gem g, int amt) {
        tokens.put(g, tokens.get(g) - amt);
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
        reserveCards.add(c);

        return true;
    }

    public void removeReserveCard(int pos) {
        reserveCards.remove(pos);
    }

    public void addNobleTile(NobleTile noble) {
        ownedNobles.add(noble);
        addPoints(noble.getPoints());
    }

    public int getNumberOfCards() {
        int sum = 0;
        for (Gem g : Gem.values()) {
            sum += production.get(g);
        }
        return sum;
    }

    @Override
    public int compareTo(Player p) {
        if (this.getPoints() == p.getPoints()) {
            return this.getNumberOfCards() - p.getNumberOfCards();
        }
        return p.getPoints() - this.getPoints();
    }



}
