package app;

import java.util.*;
import item.*;
import java.lang.*;

public class Game {

    private static int playerNumber;
    private static List<Player> players;
    private static HashMap<Gem, Integer> bank = new HashMap<Gem, Integer>(Gem.values().length);
    private static Deck[] decks = new Deck[3];
    private static Card[][] market = new Card[3][4];
    private static ArrayList<NobleTile> nobles = new ArrayList<>();

    public Game(int playerNumber) {
        this.playerNumber = playerNumber;
        this.nobles = new ArrayList<NobleTile>(playerNumber + 1);
        this.players = new ArrayList<>(playerNumber);

        for (Gem g : Gem.values()) {
            bank.put(g, 7 - (4 - playerNumber));
        }

        setPlayerArray(playerNumber);

    }

    public static void setPlayerArray(int playerNumber) {
        Scanner sc = new Scanner(System.in);
        System.out.println("The first player is the youngest.");
        for (int i = 0; i < playerNumber; i++) {
            System.out.print("Enter player name: ");
            String name = sc.nextLine();

            Player player = new Player(name);
            players.add(player);
        }

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int playerNumber = 0;
        System.out.print("Enter number of players: ");
        playerNumber = sc.nextInt();

        while (playerNumber > 4 || playerNumber < 2) {
            System.out.println("Invalid number of players, enter number between 2 and 4");
            sc.nextLine();
            System.out.println("Enter number of players: ");
            playerNumber = sc.nextInt();

        }

        Game game = new Game(playerNumber);
    }

    public static void printBoard() {
        System.out.printf("------------------------------------------------------------------\n");
        System.out.printf("Bank: ");
        System.out.printf(bank.get(Gem.Diamond) + "D , ");
        System.out.printf(bank.get(Gem.Ruby) + "R , ");
        System.out.printf(bank.get(Gem.Sapphire) + "S , ");
        System.out.printf(bank.get(Gem.Emerald) + "E , ");
        System.out.printf(bank.get(Gem.Onyx) + "O , ");
        System.out.printf(bank.get(Gem.Gold) + "G\n");

        for (int i = 1; i <= 3; i++) {
            System.out.printf("Deck <%d>\n", i); 
            for (int j = 1; j <= 4; j++) {
                System.out.printf("%d.%d %s\n", i, j, market[i][j].toString());
            }
        }
        System.out.printf("------------------------------------------------------------------\n");

        System.out.printf("<NOBLE TILES>\n");  
        for (int i = 0; i < nobles.size(); i++) {
            System.out.printf("%s\n", nobles.get(i).toString());
        }
        System.out.printf("------------------------------------------------------------------\n\n");
    }

    // Overloaded printPlayer method 1
    public static void printPlayer(String name) {
        // incomplete
    }

    // Overloaded printPlayer method 2
    public static void printPlayer(int no) {

    }

    public static void printAllPlayers(){

    }

    public static void printCommandList() {

    }

}
