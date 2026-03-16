package item;

import java.util.*;
import java.lang.*;
import util.*;
// Gem, Card, NobleTile

public class Player implements Comparable<Player> {

    private String name;

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

    public void addPoints(int p) {
        points += p;
    }

    public void addCard(Card c, HashMap<Gem, Integer> remainingGems) {
        tokens = remainingGems;
        addPoints(c.getPOINTS());
        addProduction(c.getGEMTYPE());
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
                        addPoints(c.getPOINTS());
                        addProduction(c.getGEMTYPE());
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
                addPoints(c.getPOINTS());
                addProduction(c.getGEMTYPE());
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
        System.out.println("------------------------------------------------------------------");
        System.out.printf("Player Name: %s\n", name);
        System.out.printf("Player Number: %d\n", 0);
        System.out.printf("Gems: ");
        displayTokens();
        System.out.println("Reserved: ");
        System.out.printf("* to add");
        // displayReserved();
        System.out.print("Produces: ");
        displayProduction();
        System.out.printf("Prestige: %d\n", points);
        System.out.printf("------------------------------------------------------------------\n\n");

        String output = "{ Player [" + name + "]\n";
        output += "   Points     : " + points + "\n";
        output += "   Production : " + displayProduction();
        // output += "   NumberCards: " + getNumberOfCards() + "\n";
        output += "   Nobles     : " + displayNobles() + "\n";
        output += "   Tokens     : " + displayTokens();
        output += " }";
        return output;
    }

    public String displayProduction() {

        String output = "\n";
        for (Gem g : Gem.values()) {
            output += "     " + g + " = " + production.get(g) + "\n";
        }
        return output;
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
        String output = "\n";
        for (Gem g : Gem.values()) {
            output += "     " + g + " = " + tokens.get(g) + "\n";
        }
        return output;
    }

}
