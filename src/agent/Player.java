package agent;

import item.*;
import java.lang.*;
import java.util.*;
import util.*;
// Gem, Card, NobleTile

/**
 * Represents a {@link Player} in the game. A {@link Player} has a name, tokens,
 * reserved {@link Card}s, bonuses levels, nobles who visited them, and points.
 */
public class Player implements Comparable<Player> {

    private String name;
    private int order;

    private HashMap<Gem, Integer> tokens = new HashMap<>(Gem.values().length);

    public static final int MAX_RESERVE_HAND_SIZE = 3;
    private List<Card> reserveCards = new ArrayList<>(MAX_RESERVE_HAND_SIZE);

    private HashMap<Gem, Integer> bonuses = new HashMap<>(Gem.values().length);
    private List<NobleTile> ownedNobles = new ArrayList<>();

    private int points = 0;

    /**
     * No argument constructor. {@link Player} starts with 0 tokens for every
     * Gem type.
     */
    public Player() {
        this.name = "no name";
        for (Gem g : Gem.values()) {
            tokens.put(g, 0);
            bonuses.put(g, 0);
        }

    }

    /**
     * {@link Player} starts with 0 Gems for every type. {@link Player} has a
     * name and turn order.
     *
     * @param name Name of {@link Player}
     * @param order {@link Player} order number
     */
    public Player(String name, int order) {
        this();
        this.name = name;
        this.order = order;
    }

    /**
     * Get number of prestige points of the {@link Player}.
     *
     * @return {@link Player}’s points.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Get size of {@link Player}'s reserve hand.
     *
     * @return {@link Player}’s reserve hand size
     */
    public int getReserveHandSize() {
        return reserveCards.size();
    }

    /**
     * Get {@link Player}'s reserve hand
     *
     * @return {@link Player}'s reserve hand
     */
    public List<Card> getReserveHand() {
        return reserveCards;
    }

    /**
     * Get tokens owned by the {@link Player}.
     *
     * @return {@link Player}'s tokens
     */
    public HashMap<Gem, Integer> getTokens() {
        return tokens;
    }

    /**
     * Get respective {@link Gem} production levels of the {@link Player}.
     *
     * @return HashMap of {@link Gem} production
     */
    public HashMap<Gem, Integer> getBonuses() {
        return bonuses;
    }

    /**
     * Gets {@link NobleTile}s owned by {@link Player}.
     *
     * @return List of owned {@link NobleTile}
     */
    public List<NobleTile> getOwnedNobleTile() {
        return ownedNobles;
    }

    /**
     * Adds the specified number of tokens of {@link Gem} type g to
     * {@link Player}’s tokens.
     *
     * @param g {@link Gem} type
     * @param amt Amount of token to add
     */
    public void addToken(Gem g, int amt) {
        tokens.put(g, tokens.get(g) + amt);
    }

    /**
     * Removes the specified number of tokens of {@link Gem} type g from
     * {@link Player}’s tokens.
     *
     * @param g {@link Gem} type
     * @param amt Amount of token to remove
     */
    public void removeToken(Gem g, int amt) {
        tokens.put(g, tokens.get(g) - amt);
    }

    /**
     * Adds the specified number of tokens of {@link Gem} type g to
     * {@link Player}’s bonuses.
     *
     * @param g {@link Gem} type
     * @param amt Amount of token to add
     */
    public void addBonuses(Gem g, int amt) {
        bonuses.put(g, bonuses.get(g) + amt);
    }

    /**
     * Adds 1 token of Gem {@link Gem} g to {@link Player}’s bonuses.
     *
     * @param g {@link Gem} type
     */
    public void addBonuses(Gem g) {
        this.addBonuses(g, 1);
    }

    /**
     * Adds points to {@link Player}’s points.
     *
     * @param p Points to be added to the {@link Player}
     */
    public void addPoints(int p) {
        points += p;
    }

    /**
     * Calculates the cost after subtracting from production
     *
     * @param cost Hashmap of {@link Gem} and respective their quantities.
     */
    public void discountCost(HashMap<Gem, Integer> cost) {
        for (Gem g : Gem.values()) {
            int reducedCost = cost.get(g) - bonuses.get(g);
            cost.replace(g, reducedCost < 0 ? 0 : reducedCost);
        }
    }

    /**
     * Adds {@link Card} to {@link Player}. Updates the player's remaining
     * tokens, and adds points and {@link Gem} bonuses.
     *
     * @param c the {@link Card} to purchase.
     * @param remainingGems a HashMap of the player's remaining gems
     */
    public void addCard(Card c, HashMap<Gem, Integer> remainingGems) {
        tokens = remainingGems;
        addPoints(c.getPoints());
        addBonuses(c.getGemType());
    }

    /**
     * Adds this {@link Card} to {@link Player}’s reserve hand. Does not
     * increment gold.
     *
     * @param c {@link Card} to be reserved
     */
    public void reserveCard(Card c) {
        reserveCards.add(c);
    }

    /**
     * Updates points, production levels, and the {@link Player}’s remaining
     * tokens after the purchase of the {@link Card}.
     *
     * @param c {@link Card} to be purchased
     * @param keyboard takes in user input for number of {@link Gem}
     *
     * @return True if able to buy {@link Card}. False if otherwise.
     */
    public boolean buyCard(Card c, Scanner keyboard) {
        int startingGold = tokens.get(Gem.Gold);
        int gold = startingGold;
        boolean needGold = false;

        HashMap<Gem, Integer> discountCardCost = Utility.generateHashMapClone(c.getTokens());
        discountCost(discountCardCost);
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

        boolean canUseGold = tokensLeft.get(Gem.Gold) != 0
                && Utility.getTotalGems(discountCardCost) != (startingGold - gold);

        if (!needGold) {
            if (!canUseGold) {
                addCard(c, tokensLeft);
                return true;
            } else {
                String message = "Gold may be spent to pay for the cost. Do you wish to pay gold? (Y/N): ";
                boolean isSpendingGold = Utility.willProceed(keyboard, message);

                if (!isSpendingGold) {
                    addCard(c, tokensLeft);
                    return true;
                } else {
                    int goldToSpend = Math.min(tokensLeft.get(Gem.Gold), Utility.getTotalGems(discountCardCost));
                    String goldPrompt = "Enter how much gold to spend (1 - " + goldToSpend + "): ";
                    int spentGold = Utility.askForNum(keyboard, 1, goldToSpend, goldPrompt);

                    if (spentGold == Utility.getTotalGems(discountCardCost)) {
                        removeToken(Gem.Gold, spentGold);
                        addPoints(c.getPoints());
                        addBonuses(c.getGemType());
                        return true;
                    }

                    int currGoldSpent = 0;
                    while (currGoldSpent < spentGold) {
                        String gemPrompt = "Enter a gem to discount(Diamond, Ruby, Sapphire, Emerald, Onyx): ";
                        Gem discountGem = Utility.askForGem(keyboard, gemPrompt);

                        if (discountCardCost.get(discountGem) == 0) {
                            System.out.println("‼️ Can't discount this gem! Try again! ‼️");
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

            String message = "You must spend " + necessaryGold + " of your gold to buy this card. Proceed? (Y/N): ";
            boolean willSpendGold = Utility.willProceed(keyboard, message);

            if (!willSpendGold) {
                return false;
            }

            if (!canUseGold || necessaryGold == Utility.getTotalGems(discountCardCost)) {
                addCard(c, tokensLeft);
                return true;
            }

            int goldToSpend = Math.min(tokensLeft.get(Gem.Gold), Utility.getTotalGems(discountCardCost) - necessaryGold);
            String message2 = "You can spend up to " + goldToSpend + " more gold if you want to. Will you spend more gold? (Y/N): ";

            boolean spendingMoreGold = Utility.willProceed(keyboard, message2);

            if (!spendingMoreGold) {
                addCard(c, tokensLeft);
                return true;
            }

            String goldPrompt = "Enter how much gold to spend (1 - " + goldToSpend + "): ";
            int spentGold = Utility.askForNum(keyboard, 1, goldToSpend, goldPrompt);

            if (spentGold + necessaryGold == Utility.getTotalGems(discountCardCost)) {
                removeToken(Gem.Gold, spentGold + necessaryGold);
                addPoints(c.getPoints());
                addBonuses(c.getGemType());
                return true;
            }

            int currGoldSpent = 0;
            while (currGoldSpent < spentGold) {
                String gemPrompt = "Enter a gem to discount(diamond, ruby, sapphire, emerald, onyx): ";
                Gem discountGem = Utility.askForGem(keyboard, gemPrompt);

                if (tokensLeft.get(discountGem) + 1 > discountCardCost.get(discountGem)) {
                    System.out.println("‼️ Can't discount this gem! Try again! ‼️");
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

    /**
     * Removes this {@link Card} from {@link Player}’s reserve hand.
     *
     * @param pos The index of the {@link Card} that is to be removed from the
     * {@link Player}’s hand.
     */
    public void removeReserveCard(int pos) {
        reserveCards.remove(pos);
    }

    /**
     * Removes this {@link Card} from {@link Player}’s reserve hand.
     *
     * @param card {@link Card} that is to be removed from the {@link Player}’s
     * hand.
     */
    public void removeReserveCard(Card card) {
        reserveCards.remove(card);
    }

    /**
     * Adds a {@link NobleTile} to {@link Player}’s list of visiting nobles.
     *
     * @param noble The {@link NobleTile} to be added
     */
    public void addNobleTile(NobleTile noble) {
        ownedNobles.add(noble);
        addPoints(noble.getPoints());
    }

    /**
     * Get number of {@link Card}s the {@link Player} has purchased.
     *
     * @return the total number of {@link Card}s the {@link Player} has
     * purchased thus far.
     */
    public int getNumberOfCards() {
        int sum = 0;
        for (Gem g : Gem.values()) {
            sum += bonuses.get(g);
        }
        return sum;
    }

    /**
     * Compares 2 {@link Player}s in descending order of points. If points tie,
     * return in ascending number of {@link Card}s.
     *
     * @param p The other {@link Player}
     */
    @Override
    public int compareTo(Player p) {
        if (this.getPoints() == p.getPoints()) {
            return this.getNumberOfCards() - p.getNumberOfCards();
        }
        return p.getPoints() - this.getPoints();
    }

    /**
     * Displays {@link Player}'s info on console.
     *
     * @return String of {@link Player}'s info to be displayed on console
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Player No. ").append(order).append("\n");
        sb.append("Player Name: ").append(name).append("\n");
        sb.append("Gems: ").append(displayTokens());
        sb.append("Bonuses: ").append(displayBonuses());
        sb.append("Reserved: ").append(reserveCards.size()).append("\n");
        sb.append("Prestige: ").append(points).append("\n");

        return sb.toString();
    }

    /**
     * Displays the respective {@link Gem} production levels of the
     * {@link Player}.
     *
     * @return String that displays {@link Player}’s production levels
     */
    public String displayBonuses() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Gem g : Gem.values()) {
            if (bonuses.get(g) != 0) {
                if (first) {
                    sb.append(bonuses.get(g)).append(Utility.fromGemToColour(g));
                    first = false;

                } else {
                    sb.append(", ").append(bonuses.get(g)).append(Utility.fromGemToColour(g));
                }
            }
        }

        if (first) {
            sb.append("N/A");
        }
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * Displays {@link NobleTile} that the {@link Player} owns.
     *
     * @return String that displays {@link NobleTile} that have visited the
     * {@link Player}
     */
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

    /**
     * Gets the name of the {@link Player};
     *
     * @return The name of the {@link Player}
     */
    public String getName() {
        return name;
    }

    /**
     * Return String that displays all tokens that {@link Player} has.
     *
     * @return String that displays the tokens in the {@link Player}’s hands
     */
    public String displayTokens() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Gem g : Gem.values()) {
            if (first) {
                sb.append(tokens.get(g)).append(Utility.fromGemToColour(g));
                first = false;
            } else {
                sb.append(", ").append(tokens.get(g)).append(Utility.fromGemToColour(g));
            }
        }
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * Prints all reserved {@link Card}s from a {@link Player}.
     */
    public void printReserved() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reserved cards: \n");

        if (reserveCards.isEmpty()) {
            sb.append("[N/A]\n");
        } else {
            for (int i = 0; i < reserveCards.size(); i++) {
                sb.append(i + 1).append(". ");
                sb.append(reserveCards.get(i)).append("\n");
            }
        }

        System.out.println(sb.toString());
    }

    /**
     * Gets the total number of tokens the {@link Player} has in their hand.
     *
     * @return int The number of tokens the {@link Player} has.
     *
     */
    public int getTokenAmount() {
        int total = 0;
        for (Integer i : tokens.values()) {
            total += i;
        }
        return total;
    }

    /**
     * Gets the {@link Player} turn order number
     *
     * @return order number of {@link Player}
     */
    public int getOrder() {
        return order;
    }

    /**
     * Prompts the {@link Player} to select gems to return, if the number of
     * tokens in their hand exceeds 10.
     *
     * @return a hashmap of the tokens to return to bank.
     */
    public HashMap<Gem, Integer> getReturnAmt() {
        HashMap<Gem, Integer> returnAmt = new HashMap<>();
        HashMap<Gem, Integer> pTokens = getTokens();
        int total = getTokenAmount();
        System.out.println("\nGems: " + displayTokens());
        while (total > 10) {
            System.out.println("‼️ You have more than 10 tokens. ‼️");
            System.out.println("You have to return " + (total - 10) + " tokens. ");
            Gem g = Utility.askForGem(new Scanner(System.in),
                    "Return 1 token (\u001B[34md\u001B[0m/\u001B[31mr\u001B[0m/\u001B[35ms\u001B[0m/\u001B[32me\u001B[0m/\u001B[90mo\u001B[0m), or 'cancel' to reset: ", true);
            if (g == null) {
                System.out.println("Reseting return amounts.\n");
                total = getTokenAmount();
                returnAmt.clear();
                continue;
            }

            Integer amtPerGem = returnAmt.get(g);
            if (amtPerGem == null) {
                amtPerGem = 0;
            }

            // check that player has at least the amt they want to return
            if (pTokens.get(g) >= ++amtPerGem) {
                returnAmt.put(g, amtPerGem);
                System.out.println("");
                total--;
            } else {
                System.out.println("‼️ You don't have enough tokens for that. ‼️\n");
            }
        }

        return returnAmt;
    }
}
