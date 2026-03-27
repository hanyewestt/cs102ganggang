package display;

import java.util.*; 
import item.*;
import util.*;

public class Display {
    
    /**
     * Prints round number and player name.
     */
    public static void displayRoundAndPlayer(String name, int roundNumber) {
        System.out.println("\n---------- Round " + roundNumber + " ⚔️ ----------");
        System.out.println("\n=== " + name + "'s turn ===\n");
    }

    /**
     * Prints turn options the player can take.
     */
    public static void turnOptionDisplay() {
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
     * 
     * @param bank the bank
     * @param nobles the nobles
     * @param market the market
     */
    public static void printBoard(HashMap<Gem, Integer> bank, List<NobleTile> nobles, Card[][] market) {
        System.out.printf("------------------------------------------------------------------\n");
        
        printBank(bank);

        printMarket(market);
        
        System.out.printf("------------------------------------------------------------------\n");

        Display.printNobles(nobles);

        System.out.printf("------------------------------------------------------------------\n\n");
    }

    /**
     * Prints the cards currently out in the market.
     * 
     * @param market the market
     */
    public static void printMarket(Card[][] market) {
        System.out.println("\n    [ Bonuses  | Prestige | Card Costs     ]\n");

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
        System.out.printf("Bank: ");
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

        System.out.printf("<NOBLE TILES>\n");
        System.out.println("    [ Prestige | Card Costs ]");
        for (int i = 0; i < nobles.size(); i++) {
            System.out.printf("%d   %s\n", i+1, nobles.get(i));
        }
    }

    /**
     * Prints the winners of the game.
     * 
     * @param winnningPlayers a list of {@link Player} that have won the game
     */
    public static void printWinner(List<Player> winningPlayers) {
        clearScreen();

        System.out.printf("------------------------------------------------------------------\n");
        System.out.printf(" ˖.𖥔 ݁ ˖ ⊹ ࣪ ˖ THE WINNERS ARE  ˖.𖥔 ݁ ˖ ⊹ ࣪ ˖\n");
        for (Player p : winningPlayers) {
            System.out.printf(" > ");
            System.out.println(p.getName());
        }
        System.out.printf("------------------------------------------------------------------\n");
    }

    /**
     * Clears the terminal.
     */
    public static void clearScreen() {
        System.out.print("\033c");
    }

    /**
     * Prompts user to enter a player to display by entering their number
     * 
     * @param sc Scanner
     * @param totalPlayers the number of total players
     * @return the selected player’s order number
     */
    public static int printPlayerNo(Scanner sc, int totalPlayers) {
        String display = String.format("Enter player number (1 - %d), 0 to cancel: ", totalPlayers);

        int choice = Utility.askForNum(sc, 0, totalPlayers, display);
        return choice - 1;
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
                
                costDisplay += "" + entry.getValue() + Utility.fromGemToChar((Gem)entry.getKey());
                
            }
        }

        return costDisplay;
    }
}
