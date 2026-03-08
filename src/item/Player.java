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

    public void buyCard(Card card) {
        HashMap<Gem, Integer> cardCost = card.getTokens();
        HashMap<Gem, Integer> playerTokens = this.getTokens();
        for (Gem g: tokens.keySet()) {
            if (cardCost.get(g) > playerTokens.get(g)) {
                this.removeToken(g, playerTokens.get(g));
            }
            this.removeToken(g, cardCost.get(g));
        }        
    }

    public void convertGold(HashMap<Gem, Integer> playerTokens, Card card) {
        HashMap<Gem, Integer> cardCost = card.getTokens();
        int difference = 0;

        for (int i = 0; i < Gem.values().length - 1; i++) {
            difference += cardCost.get(Gem.values()[i]) - playerTokens.get(Gem.values()[i]);
        }

        int gold = playerTokens.get(Gem.Gold);
        if (difference == gold) {
            buyCard(card);
            return;
        }

        if (difference > gold) {
            return; //possibly throw error? see how the player action is actually handled
        }

        if (difference < gold) {
            Scanner sc = new Scanner(System.in);
            System.out.print("How many gold> ");
            int numUsed = sc.nextInt();

            this.removeToken(Gem.Gold, numUsed);
        }
    }
}
