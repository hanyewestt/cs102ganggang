package item;

import java.lang.*;
import java.util.*;
import util.*;
// Gem, Card, NobleTile

public class Player implements Comparable<Player> {

    private String name;
    private int order;

    private HashMap<Gem, Integer> tokens = new HashMap<>(Gem.values().length);

    private static final int MAX_RESERVE_HAND_SIZE = 3;
    private List<Card> reserveCards = new ArrayList<>(MAX_RESERVE_HAND_SIZE);

    private HashMap<Gem, Integer> production = new HashMap<>(Gem.values().length);
    private List<NobleTile> ownedNobles = new ArrayList<>(5);

    private int points = 0;

    public Player() {
        this.name = "no name";
        for (Gem g : Gem.values()) {
            tokens.put(g, 0);
            production.put(g, 0);
        }

    }

    public Player(String name, int order) {
        this();
        this.name = name;
        this.order = order;
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

    public void addProduction(Gem g, int amt) {
        production.put(g, production.get(g) + amt);
    }

    public void addProduction(Gem g) {
        this.addProduction(g, 1);
    }

    public void addPoints(int p) {
        points += p;
    }

    public void addCard(Card c, HashMap<Gem, Integer> remainingGems) {
        tokens = remainingGems;
        addPoints(c.getPoints());
        addProduction(c.getGemType());
    }

    public void reserveCard(Card c) {
        reserveCards.add(c);
    }

    public boolean buyCard(Card c, Scanner keyboard) {
        int startingGold = tokens.get(Gem.Gold);
        int gold = startingGold;
        boolean needGold = false;

        HashMap<Gem, Integer> discountCardCost = c.getTokens();
        for (Gem gem : Gem.values()) {
            int discountedCost = discountCardCost.get(gem) - production.get(gem);

            if (discountedCost < 0) {
                discountedCost = 0;
            }

            discountCardCost.replace(gem, discountedCost);
        }

        HashMap<Gem, Integer> tokensLeft = new HashMap<>();

        for (Gem gem : Gem.values()) {
            if (gem.equals(Gem.Gold)) {
                continue;
            }

            int difference = tokens.get(gem) - discountCardCost.get(gem);
            if (difference >= 0) {
                tokensLeft.put(gem, difference);
            } else if (Math.abs(difference) > gold) {
                return false;
            } else {
                needGold = true;
                tokensLeft.put(gem, 0);
                gold -= Math.abs(difference);
            }
        }
        tokensLeft.put(Gem.Gold, gold);

        boolean canUseGold = tokensLeft.get(Gem.Gold) != 0;

        if (!needGold) {
            if (!canUseGold) {
                addCard(c, tokensLeft);
                return true;
            } else {
                String message = "Gold may be spent to pay for the cost. Do you wish to pay gold? {Y/N}:";
                boolean isSpendingGold = Utility.willProceed(keyboard, message);

                if (!isSpendingGold) {
                    addCard(c, tokensLeft);
                    return true;
                } else {
                    int goldToSpend = Math.min(tokensLeft.get(Gem.Gold), Utility.getTotalGems(discountCardCost));
                    String goldPrompt = "Enter how much gold to spend (1 - " + goldToSpend + "):";
                    int spentGold = Utility.askForNum(keyboard, 1, goldToSpend, goldPrompt);

                    if (spentGold == Utility.getTotalGems(discountCardCost)) {
                        removeToken(Gem.Gold, spentGold);
                        addPoints(c.getPoints());
                        addProduction(c.getGemType());
                        return true;
                    }

                    int currGoldSpent = 0;
                    while (currGoldSpent < spentGold) {
                        String gemPrompt = "Enter a gem to discount(Diamond, Ruby, Sapphire, Emerald, Onyx):";
                        Gem discountGem = Utility.askForGem(keyboard, gemPrompt);

                        if (discountCardCost.get(discountGem) == 0) {
                            System.out.println("Can't discount this gem! Try again!");
                            continue;
                        }

                        discountCardCost.replace(discountGem, discountCardCost.get(discountGem) - 1);
                        tokensLeft.replace(discountGem, tokensLeft.get(discountGem) + 1);

                        tokensLeft.replace(Gem.Gold, tokensLeft.get(Gem.Gold) - 1);
                        currGoldSpent++;
                    }

                    addCard(c, tokensLeft);
                    return true;
                }
            }
        } else {
            int necessaryGold = startingGold - tokensLeft.get(Gem.Gold);

            String message = "You must spend " + necessaryGold + " of your gold to buy this card. Proceed? (Y/N):";
            boolean willSpendGold = Utility.willProceed(keyboard, message);

            if (!willSpendGold) {
                return false;
            }

            if (!canUseGold || necessaryGold == Utility.getTotalGems(discountCardCost)) {
                addCard(c, tokensLeft);
                return true;
            }

            int goldToSpend = Math.min(tokensLeft.get(Gem.Gold), Utility.getTotalGems(discountCardCost) - necessaryGold);
            String message2 = "You can spend up to " + goldToSpend + " more gold if you want to. Will you spend more gold? (Y/N):";

            boolean spendingMoreGold = Utility.willProceed(keyboard, message2);

            if (!spendingMoreGold) {
                addCard(c, tokensLeft);
                return true;
            }

            String goldPrompt = "Enter how much gold to spend (1 - " + goldToSpend + "):";
            int spentGold = Utility.askForNum(keyboard, 1, goldToSpend, goldPrompt);

            if (spentGold + necessaryGold == Utility.getTotalGems(discountCardCost)) {
                removeToken(Gem.Gold, spentGold + necessaryGold);
                addPoints(c.getPoints());
                addProduction(c.getGemType());
                return true;
            }

            int currGoldSpent = 0;
            while (currGoldSpent < spentGold) {
                String gemPrompt = "Enter a gem to discount(Diamond, Ruby, Sapphire, Emerald, Onyx):";
                Gem discountGem = Utility.askForGem(keyboard, gemPrompt);

                if (tokensLeft.get(discountGem) + 1 > discountCardCost.get(discountGem)) {
                    System.out.println("Can't discount this gem! Try again!");
                    continue;
                }

                tokensLeft.replace(discountGem, tokensLeft.get(discountGem) + 1);
                tokensLeft.replace(Gem.Gold, tokensLeft.get(Gem.Gold) - 1);
                currGoldSpent++;
            }

            addCard(c, tokensLeft);
            return true;
        }
    }

    public void removeReserveCard(int pos) {
        reserveCards.remove(pos);
    }

    public void removeReserveCard(Card card) {
        reserveCards.remove(card);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Player No. ").append(order).append("\n");
        sb.append("Player Name: ").append(name).append("\n");
        sb.append("Gems: ").append(displayTokens());
        sb.append("Produces: ").append(displayProduction());
        sb.append("Reserved: ").append(reserveCards.size()).append("\n");
        sb.append("Prestige: ").append(points).append("\n");

        return sb.toString();
    }

    public String displayProduction() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Gem g : Gem.values()) {
            if (production.get(g) != 0) {
                if (first) {
                sb.append(production.get(g)).append(Utility.fromGemToChar(g));
                first = false;

            } else {
                sb.append(", ").append(production.get(g)).append(Utility.fromGemToChar(g));
            }
            }
        }

        if (first) {
            sb.append("N/A");
        }
        sb.append("]\n");

        return sb.toString();
    }

    public String displayNobles() {

        if (ownedNobles.size() == 0) {
            return "None";
        }

        String output = "";
        int size = ownedNobles.size() - 1;
        for (int idx = 0; idx < size; idx++) {
            NobleTile noble = ownedNobles.get(idx);
            output += noble + ",";
        }
        output += ownedNobles.get(size);
        output += "\n";
        return output;
    }

    public String getName() {
        return name;
    }

    public String displayTokens() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Gem g : Gem.values()) {
            if (first) {
                sb.append(tokens.get(g)).append(Utility.fromGemToChar(g));
                first = false;
            } else {
                sb.append(", ").append(tokens.get(g)).append(Utility.fromGemToChar(g));
            }
        }
        sb.append("]\n");

        return sb.toString();
    }

    public void printReserved() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reserved cards: ");

        if (reserveCards.isEmpty()) {
            sb.append("[N/A]\n");
        } else {
            for (int i = 0; i < reserveCards.size(); i++) {
                sb.append("1.").append(i).append(" ");
                sb.append(reserveCards.get(i).toString()).append("\n");
            }
        }

        System.out.println(sb.toString());
    }
}
