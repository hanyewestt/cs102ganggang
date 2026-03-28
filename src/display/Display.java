package display;

import java.util.*;
import item.*;
import util.*;
import app.*;

public class Display {

    private static Game splendor;
    private static Card[][] market;
    private static Map<Gem, Integer> bank;

    public Display(Game splendor) {
        this.splendor = splendor;
        market = splendor.getMarket();
        bank = splendor.getBank();
        //..
    }

    /**
     * Prints turn options the player can take.
     */
    public static void turnOptionDisplay(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------- Moves 🎮 -----------------\n");
        sb.append("1. Draw tokens\n");
        sb.append("2. Reserve a card\n");
        sb.append("3. Buy a card\n");
        sb.append("4. Show reserved cards\n");
        sb.append("5. Display other players\n");
        sb.append("6. admin perms");

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
     * Prints options the player has to buy cards.
     */
    public static void buyCardDisplay() {
        System.out.println();
        System.out.println("1. Buy from market");
        System.out.println("2. Buy from reserve");
        System.out.println("0. Cancel");
        System.out.println();
    }

    /**
     * Prints options the player has to draw tokens.
     */
    public static void drawTokenDisplay() {
        System.out.println();
        System.out.println("Token options: ");
        System.out.println("1. Take 3 different tokens");
        System.out.println("2. Take 2 same tokens");
        System.out.println("0. Cancel");
        System.out.println();
    }

    /**
     * Prints options the player has to reserve cards.
     */
    public static void reserveCardDisplay() {
        System.out.println();
        System.out.println("Choose option: ");
        System.out.println("1. Reserve from market");
        System.out.println("2. Reserve from deck");
        System.out.println("0. Cancel");
        System.out.println();
    }

    /**
     * Prints the current state of the board, including all cards and noble
     * tiles. Includes: Avaliable cards, Bank, Nobile Tiles
     *
     * @param bank the bank
     * @param nobles the nobles
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

        printMarket(market);
        System.out.println();

        System.out.println("----------------- Nobles 👑 -----------------\n");

        Display.printNobles(nobles);
        System.out.println();

        System.out.println("---------------- Your Hand 👤 ---------------\n");

        System.out.println(player);
    }

    /**
     * Prints the cards currently out in the market.
     *
     * @param market the market
     */
    public static void printMarket(Card[][] market) {
        System.out.println("    [ Bonuses  | Prestige | Card Costs 💰  ]\n");

        for (int i = 1; i <= 3; i++) {
            System.out.printf("Deck <%d>\n", i);
            for (int j = 1; j <= 4; j++) {
                if (market[i - 1][j - 1] == null) {
                    System.out.printf("%d.%d [ Empty ]\n", i, j);

                } else {
                    System.out.printf("%d.%d %s\n", i, j, market[i - 1][j - 1].toString());
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
        System.out.printf(bank.get(Gem.Diamond) + "D , ");
        System.out.printf(bank.get(Gem.Ruby) + "R , ");
        System.out.printf(bank.get(Gem.Sapphire) + "S , ");
        System.out.printf(bank.get(Gem.Emerald) + "E , ");
        System.out.printf(bank.get(Gem.Onyx) + "O , ");
        System.out.printf(bank.get(Gem.Gold) + "G\n");
    }

    /**
     * Prints {@link NobleTile}s on the board.
     *
     * @param nobles a list of {@link NobleTile} to print
     */
    public static void printNobles(List<NobleTile> nobles) {
        System.out.println("     [ Prestige | Bonus Req. ]\n");
        for (int i = 0; i < nobles.size(); i++) {
            System.out.printf("%d.   %s\n", i + 1, nobles.get(i));
        }
    }

    /**
     * Prints the winners of the game.
     *
     * @param winnningPlayers a list of {@link Player} that have won the game
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
        // System.out.print("\033c");
    }

    /**
     * Prompts the current player to select which players' hands they wish to
     * view and returns a map of the chosen {@link Player}s.
     *
     * @param sc the Scanner used to read input from the keyboard
     * @param players the list of {@link Player}s
     * @param player the current {@link Player}
     * @return a set of player numbers selected by the current player
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

            if (!playersChosen.containsValue(players.get(choice - 1))) {
                playersChosen.put(choice, players.get(choice - 1));
            }
        }

        return playersChosen;
    }

    /**
     * Prints other {@link Player}s' hand.
     *
     * @param playersToPrint
     */
    public static void printOtherPlayers(Map<Integer, Player> playersToPrint) {

        if (!playersToPrint.isEmpty()) {
            System.out.println("------------- Other players 👥 -------------\n");

            for (Player p : playersToPrint.values()) {
                System.out.println(p);
            }
        }
    }

    public static void printReserved(boolean toPrintReserved, Player player) {
        if (toPrintReserved) {
            System.out.println("---------------- Reserved 🎒 ----------------\n");
            player.printReserved();
        }
    }

    /**
     * Formats a printable string for card costs
     *
     * @param tokens a HashMap of the card costs to print
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

                costDisplay += "" + entry.getValue() + Utility.fromGemToChar((Gem) entry.getKey());

            }
        }

        return costDisplay;
    }

    /**
     * If any of the following actions are possible, hide the skip option.
     * Possible actions: 1. Draw tokens 2. Buy cards 3. Reserve cards. Calls
     * {@link #showDrawToken()}, {@link #showBuyCard()}, {@link #showReserveCard()},
     *
     * @param player the current {@link Player}
     */
    public static boolean hideSkipOption(Player player) {
        return showDrawToken() || showBuyCard(player) || showReserveCard(player);
    }

    /**
     * Returns true if the {@link Player} can afford any of the cards. Returns
     * false otherwise.
     *
     * @param player the current {@link Player}
     * @param market market
     */
    public static boolean showBuyCard(Player player) {

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

    /**
     * Returns true if the {@link Player} can draw 2 or draw 3 tokens. Calls
     * {@link #canDrawTwo()} and {@link #canDrawThree()}.
     *
     */
    public static boolean showDrawToken() {
        // 0, 1, 2 in bank is impossible to draw from.
        if (Utility.getTotalGems(new HashMap<>(bank)) < 3) {
            return false;
        }
        return canDrawTwo() || canDrawThree();
    }

    /**
     * Returns true if the {@link Player} can draw 2 tokens. Returns true if
     * there is at least 4 of a single token type in bank. Returns false
     * otherwise.
     *
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
     * Returns true if the {@link Player} can draw 3 tokens. Returns true if
     * there is at least 3 different token types, each with more than one token
     * each in bank. Returns false otherwise.
     *
     */
    public static boolean canDrawThree() {

        int nTokensNotEmpty = 0;
        for (Gem g : Gem.values()) {
            if (g == Gem.Gold) {
                continue;
            }
            if (bank.get(g) > 0) {
                nTokensNotEmpty++;
            }
        }

        // at least 3 different tokens, at least one each.
        return (nTokensNotEmpty >= 3) ? true : false;
    }

    /**
     * Returns false if {@link Player} reserve hand size has hit the max reserve
     * hand size.
     *
     * @param player
     *
     */
    public static boolean showReserveCard(Player player) {
        return (player.getReserveHandSize() == Player.MAX_RESERVE_HAND_SIZE) ? false : true;
    }
}
