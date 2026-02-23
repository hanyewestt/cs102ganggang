package app;

import java.util.*;
import item.*;
import java.lang.*;

public class Game {

    private int playerNumber;
    private List<Player> players;
    private HashMap<Gem, Integer> bank = new HashMap<Gem, Integer>(Gem.values().length);
    private Deck[] decks = new Deck[3];
    private Card[][] market = new Card[3][4];
    private List<NobleTile> nobles;

    public Game(int playerNumber) {
        this.playerNumber = playerNumber;
        this.nobles = new ArrayList<NobleTile>(playerNumber + 1);
        this.players = new ArrayList<>(playerNumber);

        for (Gem g : Gem.values()) {
            bank.put(g, 7 - (4 - playerNumber));
        }

        setPlayerArray(playerNumber);

    }

    public void setPlayerArray(int playerNumber) {
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
}
