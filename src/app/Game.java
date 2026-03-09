package app;

import java.util.*;
import item.*;
import java.lang.*;

public class Game {

    private static int playerNumber;
    private static List<Player> players = new ArrayList<>();
    private static HashMap<Gem, Integer> bank = new HashMap<Gem, Integer>(Gem.values().length);
    private static Deck[] decks = new Deck[3];
    private static Card[][] market = new Card[3][4];
    private static List<NobleTile> nobles;
    private static Scanner sc = new Scanner(System.in); // can like that??

    public Game(int playerNumber) {
        this.playerNumber = playerNumber;
        this.nobles = new ArrayList<NobleTile>(playerNumber + 1);
        this.players = new ArrayList<>(playerNumber);

        setPlayerArray(playerNumber);

    }

    public static void setPlayerArray(int playerNumber) {
        System.out.println("\nThe first player is the youngest.");
        for (int i = 0; i < playerNumber; i++) {
            System.out.print("Enter player " + (i + 1) + " name: ");
            String name = sc.nextLine();
            Player player = new Player(name);
            players.add(player);
        }
    }

    public static int enterNumber(int min, int max) {
        boolean valid = false;
        int n = -1; // can anyhow initialise??
        while (!valid) {
            try {
                n = sc.nextInt();
                if (n < min || n > max) {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                    continue;
                }
                valid = true;
            } catch (InputMismatchException e) {
                System.out.print("Please enter a number between " + min + " and " + max + ": ");
            } finally {
                sc.nextLine();
            }
        }
        return n;
    }

    public static void turnOptionDisplay() {
        System.out.println("1. Draw tokens");
        System.out.println("2. Reserve a card");
        System.out.println("3. buy a card");
        System.out.println();
        System.out.print("Please enter your choice:");
    }

    public static void doPlayerTurn(Player player) {

        boolean turnDone = false;
        while (!turnDone) {
            turnOptionDisplay();

            switch (enterNumber(1, 4)) {
                case 1:
                    turnDone = drawToken();
                    break;
                case 2:
                    turnDone = reserveCard();
                    break;
                case 3: 
                    turnDone = buyCard();
                    if (visitingNobles().size() > 1) {
                        System.out.print("Please select a noble: ");
                        int choice = enterNumber(1, visitingNobles().size());
                        player.setNoble(); // are we setting it like that ??
                    } else if (visitingNobles().size == 1) {
                        player.setNoble(); // are we setting it like that ??
                    }
                    break;
                case 4: 
                    //skip turn????
            }
        }
    }

    public static void main(String[] args) {
        /* 
        human v human / human v computer
            System.out.println("1. human v human(s)");
            System.out.println("2. human v computer(s)");
            System.out.print("Please enter your choice:");

            if (enterNumber(1, 2) == 2) {
                System.out.print("Enter total number of players (between 2 and 4): ");
                int playerNumber = enterNumber(2, 3);

                System.out.print("Enter number of human players (between 2 and 3): ");
            } 
        */
        System.out.print("Enter number of players (between 2 and 4): ");
        int playerNumber = enterNumber(2, 4);

        Game game = new Game(playerNumber);

        boolean lastRound = false;
        int roundNumber = 1;
        while (!lastRound) {
            System.out.println("\n---------- Round " + roundNumber + " ----------");
            for (int i = 0; i < playerNumber; i++) {
                System.out.println("\n=== " + players.get(i).getName() + "'s turn ===");
                System.out.println(players.get(i));
                doPlayerTurn(players.get(i));
                lastRound = hitWinCondition(players.get(i));
            }
            roundNumber++;
            // clear terminal
        }
        List<Player> winningPlayers = getWinner();
        for (int i = 0; i < winningPlayers.size(); i++) {
            System.out.println(winningPlayers.get(i).getName());// is there a getName?
        }
        sc.close();

    }
}
