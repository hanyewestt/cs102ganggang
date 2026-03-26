package display;

import java.util.*;
import item.*;
import util.*;

public class Display {

    /**
     * Prints turn options the player can take.
     */
    public static void turnOptionDisplay() {
        System.out.println();
        System.out.println("---------------------------- Moves 🎮 ----------------------------");
        System.out.println("1. Draw tokens");
        System.out.println("2. Reserve a card");
        System.out.println("3. Buy a card");
        System.out.println("4. Show reserved cards");
        System.out.println("5. Display player");
        System.out.println("6. admin perms");
        System.out.println();
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
     */
    public static void printBoard(Player player, int roundNumber, HashMap<Gem, Integer> bank, ArrayList<NobleTile> nobles, Card[][] market) {
        System.out.println("\n---------- Round " + roundNumber + " ⚔️ ----------");
        System.out.println("\n=== " + player.getName() + "'s turn ===\n");

        System.out.println("----------------------------- Bank 🏦 -----------------------------\n");
        
        printBank(bank);
        System.out.println();

        System.out.println("---------------------------- Market 🏬 ----------------------------\n");

        printMarket(market);
        System.out.println();
        
        System.out.println("---------------------------- Nobles 👑 ----------------------------\n");

        Display.printNobles(nobles);
        System.out.println();

        System.out.println("--------------------------- Your Hand 👤 --------------------------\n");

        System.out.println(player);
    }

    /**
     * Prints the cards currently out in the market.
     */
    public static void printMarket(Card[][] market) {
        System.out.println("Example Card");
        System.out.println("    [ Gem Produced | Prestige | Card Cost          ]\n");

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
     */
    public static void printNobles(ArrayList<NobleTile> nobles) {
        for (int i = 0; i < nobles.size(); i++) {
            System.out.printf("%s\n", nobles.get(i));
        }
    }

    /**
     * Prints the winners of the game.
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
}
