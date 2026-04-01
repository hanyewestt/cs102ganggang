package display;

import agent.*;
import app.*;
import item.*;
import java.util.*;
import util.*;

/**
 * Displays information on console
 */
public class Display {

    private static Game splendor;
    private static Card[][] market;
    private static Map<Gem, Integer> bank;

    /**
     * Constructor for displaying game information on console.
     *
     * @param splendor {@link Game} to be displayed
     */
    public Display(Game splendor) {
        this.splendor = splendor;
        market = splendor.getMarket();
        bank = splendor.getBank();
        //..
    }

    /**
     * Prints turn options the {@link Player} can take.
     *
     * @param player {@link Player} who is performing their turn
     */
    public static void turnOptionDisplay(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------ Moves 🎮 -----------------\n\n");
        sb.append("1. Draw tokens\n");
        sb.append("2. Reserve a card\n");
        sb.append("3. Buy a card\n");
        sb.append("4. Show reserved cards\n");
        sb.append("5. Display other players\n");
        sb.append("6. admin perms\n");

        if (!showDrawToken()) {
            sb.insert(sb.indexOf("1."), "\u001b[9m");
            sb.insert(sb.indexOf("2."), "\u001b[0m");
        }
        if (!showReserveCard(player)) {
            sb.insert(sb.indexOf("2."), "\u001b[9m");
            sb.insert(sb.indexOf("3."), "\u001b[0m");
        }
        if (!showBuyCard(player)) {
            sb.insert(sb.indexOf("3."), "\u001b[9m");
            sb.insert(sb.indexOf("4."), "\u001b[0m");
        }

        System.out.println(sb);

    }

    /**
     * Prints options the player has to buy {@link Card}s.
     *
     * @param player {@link Player} who is performing their turn
     */
    public static void buyCardDisplay(Player player) {

        StringBuilder sb = new StringBuilder();

        sb.append("Choose option:\n");
        sb.append("1. Buy from market\n");
        sb.append("2. Buy from reserve\n");
        sb.append("0. Cancel\n");

        if (!canBuyFromMarket(player)) {
            sb.insert(sb.indexOf("1."), "\u001b[9m");
            sb.insert(sb.indexOf("2."), "\u001b[0m");
        }

        if (!canBuyFromReserve(player)) {
            sb.insert(sb.indexOf("2."), "\u001b[9m");
            sb.insert(sb.indexOf("0."), "\u001b[0m");
        }

        System.out.println(sb);
    }

    /**
     * Prints options the {@link Player} has to draw tokens.
     */
    public static void drawTokenDisplay() {

        StringBuilder sb = new StringBuilder();

        sb.append("Token options:\n");
        sb.append("1. Take up to 3 different tokens\n");
        sb.append("2. Take 2 same tokens\n");
        sb.append("0. Cancel\n");

        if (!canDrawTwo()) {
            sb.insert(sb.indexOf("2."), "\u001b[9m");
            sb.insert(sb.indexOf("0."), "\u001b[0m");
        }
        System.out.println(sb);
    }

    /**
     * Prints options the {@link Player} has to reserve {@link Card}s.
     */
    public static void reserveCardDisplay() {
        StringBuilder sb = new StringBuilder();

        sb.append("Choose option:\n");
        sb.append("1. Reserve from market\n");
        sb.append("2. Reserve from deck\n");
        sb.append("0. Cancel\n");

        System.out.println(sb);
    }

    /**
     * Prints the current state of the board, including all {@link Card}s and
     * {@link NobleTile}s Includes: Avaliable {@link Card}s, Bank,
     * {@link NobleTile}s
     *
     * @param player {@link Player} who is performing their turn
     * @param roundNumber number of the round currently being played
     * @param bank the bank
     * @param nobles the {@link NobleTile}
     * @param market the market
     */
    public static void printBoard(Player player, int roundNumber, HashMap<Gem, Integer> bank, ArrayList<NobleTile> nobles, Card[][] market) {
        String roundDisplay = roundNumber < 10 ? "\n✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦ Round " + roundNumber + " ⚔️ ✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦" : "\n✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦ Round " + roundNumber + " ⚔️ ✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦✦";
        System.out.println(roundDisplay);
        System.out.println("\n=== " + player.getName() + "'s turn ===\n");

        System.out.println("------------------ Bank 🏦 ------------------\n");

        printBank(bank);
        System.out.println();

        System.out.println("----------------- Market 🏬 -----------------\n");

        printMarket(market, player);
        System.out.println();

        System.out.println("----------------- Nobles 👑 -----------------\n");

        Display.printNobles(nobles);
        System.out.println();

        System.out.println("---------------- Your Hand 👤 ---------------\n");

        System.out.println(player);
    }

    /**
     * Prints the {@link Card}s currently out in the market.
     *
     * @param market the market }
     */
    public static void printMarket(Card[][] market, Player player) {
        System.out.println("    [ Bonuses | Prestige | Card Costs 💰   ]\n");

        for (int i = 1; i <= 3; i++) {
            System.out.printf("Deck <%d>\n", i);
            for (int j = 1; j <= 4; j++) {

                Card c = market[i - 1][j - 1];
                if (c == null) {
                    System.out.printf("%d.%d [ Empty ]\n", i, j);

                } else {
                    HashMap<Gem, Integer> playerBonuses = player.getBonuses();
                    HashMap<Gem, Integer> cardCost = Utility.generateHashMapClone(c.getTokens());
                    Utility.discount(cardCost, playerBonuses);

                    HashMap<Gem, Integer> tokensToPay = Utility.findSubtractionAmount(player.getTokens(), cardCost);
                    if (tokensToPay == null) {
                        System.out.printf("\u001b[9m%d.%d\u001b[0m %s%n", i, j, c);
                    } else {
                        System.out.printf("%d.%d %s\n", i, j, c.toString());
                    }
                }
            }

            if (i != 3) {
                System.out.println();
            }
        }
    }

    /**
     * Prints bank contents.
     *
     * @param bank the bank
     */
    public static void printBank(HashMap<Gem, Integer> bank) {
        System.out.printf(bank.get(Gem.Diamond) + Utility.fromGemToColour(Gem.Diamond) + " , ");
        System.out.printf(bank.get(Gem.Ruby) + Utility.fromGemToColour(Gem.Ruby) + " , ");
        System.out.printf(bank.get(Gem.Sapphire) + Utility.fromGemToColour(Gem.Sapphire) + " , ");
        System.out.printf(bank.get(Gem.Emerald) + Utility.fromGemToColour(Gem.Emerald) + " , ");
        System.out.printf(bank.get(Gem.Onyx) + Utility.fromGemToColour(Gem.Onyx) + " , ");
        System.out.printf(bank.get(Gem.Gold) + Utility.fromGemToColour(Gem.Gold) + "\n");
    }

    /**
     * Prints {@link NobleTile}s on the board.
     *
     * @param nobles a list of {@link NobleTile} to print
     */
    public static void printNobles(List<NobleTile> nobles) {
        System.out.println("     [ Prestige | Bonus Req.    ]\n");
        for (int i = 0; i < nobles.size(); i++) {
            System.out.printf("%d.   %s\n", i + 1, nobles.get(i));
        }
    }

    /**
     * Prints the winners of the game.
     *
     * @param winningPlayers a list of {@link Player} that have won the game
     */
    public static void printWinner(List<Player> winningPlayers) {
        clearScreen();

        String winTitle = winningPlayers.size() > 1 ? " ˖.𖥔 ݁ ˖ ⊹ ࣪ ˖ THE WINNERS ARE  ˖.𖥔 ݁ ˖ ⊹ ࣪ ˖" : " ˖.𖥔 ݁ ˖ ⊹ ࣪ ˖ THE WINNER IS  ˖.𖥔 ݁ ˖ ⊹ ࣪ ˖";
        System.out.println(winTitle);
        for (Player p : winningPlayers) {
            System.out.print(" > ");
            System.out.println(p.getName());
        }

    }

    /**
     * Clears the terminal.
     */
    public static void clearScreen() {
        System.out.print("\033c");
    }

    /**
     * Prompts the current {@link Player} to select which {@link Player}s' hands
     * they wish to view and returns a map of the chosen {@link Player}s.
     *
     * @param sc the Scanner used to read input from the keyboard
     * @param players the list of {@link Player}s
     * @param player the current {@link Player}
     *
     * @return a set of {@link Player} numbers selected by the current
     * {@link Player}
     */
    public static Map<Integer, Player> choosePlayersToPrint(Scanner sc, List<Player> players, Player player) {
        String display = String.format("Enter player number (1 - %d), 0 to finish your selection: ", players.size());

        int choice;
        Map<Integer, Player> playersChosen = new TreeMap<>();
        while (true) {
            choice = Utility.askForNum(sc, 0, players.size(), display);
            if (choice == 0) {
                break;
            }

            if (!playersChosen.containsValue(players.get(choice - 1)) && !players.get(choice - 1).getName().equals(player.getName())) {
                playersChosen.put(choice, players.get(choice - 1));
            }
        }

        return playersChosen;
    }

    /**
     * Prints other {@link Player}s' hand.
     *
     * @param playersToPrint Map of {@link Player} number and corresponding
     * {@link Player} object
     */
    public static void printOtherPlayers(Map<Integer, Player> playersToPrint) {

        if (!playersToPrint.isEmpty()) {
            System.out.println("-------------- Other players 👥 -------------\n");

            for (Player p : playersToPrint.values()) {
                System.out.println(p);
            }
        }
    }

    /**
     * Prints the reserve hand of the {@link Player}
     *
     * @param toPrintReserved if true, print {@link Player}'s reserve hand
     * @param player {@link Player} who's reserve hand is to be printed
     */
    public static void printReserved(boolean toPrintReserved, Player player) {
        if (toPrintReserved) {
            System.out.println("---------------- Reserved 🎒 ----------------\n");
            player.printReserved();
        }
    }

    /**
     * Formats a printable string for {@link Card} costs
     *
     * @param tokens a HashMap of the {@link Card} costs to print
     * @return String the printable string
     */
    public static String costDisplayString(HashMap<Gem, Integer> tokens) {
        boolean first = true;
        String costDisplay = "";

        Iterator tokenIterator = tokens.entrySet().iterator();

        while (tokenIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) tokenIterator.next();

            if ((int) entry.getValue() > 0) {
                if (first) {
                    first = false;
                } else {
                    costDisplay += ", ";
                }

                costDisplay += "" + entry.getValue() + Utility.fromGemToColour((Gem) entry.getKey());

            }
        }

        return costDisplay;
    }

    /**
     * If any of the following actions are possible, hide the skip option.
     * Possible actions: 1. Draw tokens 2. Buy cards 3. Reserve cards. Calls
     * {@link #showDrawToken()}, {@link #showBuyCard(player)}, {@link #showReserveCard(player)},
     *
     * @param player the current {@link Player}
     *
     * @return True if action can still be performed. False if otherwise.
     */
    public static boolean hideSkipOption(Player player) {
        return showDrawToken() || showBuyCard(player) || showReserveCard(player);
    }

    /**
     * Returns true if the {@link Player} can afford any of the cards. Returns
     * false otherwise.
     *
     * @param player the current {@link Player}
     *
     * @return True if {@link Player} can buy {@link Card}. False if otherwise.
     */
    public static boolean showBuyCard(Player player) {
        return canBuyFromMarket(player) || canBuyFromReserve(player);
    }

    public static boolean canBuyFromMarket(Player player) {
        HashMap<Gem, Integer> playerBonuses = player.getBonuses();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                Card c = market[i][j];
                if (c == null) {
                    continue;
                }

                HashMap<Gem, Integer> cardCost = Utility.generateHashMapClone(c.getTokens());
                Utility.discount(cardCost, playerBonuses);

                HashMap<Gem, Integer> tokensToPay = Utility.findSubtractionAmount(player.getTokens(), cardCost);
                if (tokensToPay != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canBuyFromReserve(Player player) {
        HashMap<Gem, Integer> playerBonuses = player.getBonuses();
        for (Card c : player.getReserveHand()) {

            HashMap<Gem, Integer> cardCost = Utility.generateHashMapClone(c.getTokens());
            Utility.discount(cardCost, playerBonuses);

            if (Utility.findSubtractionAmount(player.getTokens(), cardCost) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the {@link Player} bank not empty
     *
     * @return True if bank is not empty.
     */
    public static boolean showDrawToken() {
        int nTokensNotEmpty = 0;
        for (Gem g : Gem.values()) {
            if (g == Gem.Gold) {
                continue;
            }
            if (bank.get(g) > 0) {
                // at least one not-gold token that isn't empty.
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the {@link Player} can draw 2 tokens. Returns true if
     * there is at least 4 of a single token type in bank. Returns false
     * otherwise.
     *
     * @return True if {@link Player} can draw 2 tokens. False if otherwise.
     */
    public static boolean canDrawTwo() {
        for (Gem g : Gem.values()) {
            if (g == Gem.Gold) {
                continue;
            }
            if (bank.get(g) >= 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns false if {@link Player}'s reserve hand size has hit the max
     * reserve hand size.
     *
     * @param player {@link Player}'s reserve hand to show.
     *
     * @return True if {@link Player}'s reserve hand size is under the max
     * reserve hand size. False if otherwise.
     */
    public static boolean showReserveCard(Player player) {
        return player.getReserveHandSize() != Player.MAX_RESERVE_HAND_SIZE;
    }
}
