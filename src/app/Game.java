package app;

import java.util.*;
import java.lang.*;
import item.*;
import util.*;
import config.*;

public class Game {

    private static int playerNumber;
    private static List<Player> players;
    private static HashMap<Gem, Integer> bank = new HashMap<Gem, Integer>(Gem.values().length);
    private static ArrayList<Deck<Card>> decks = new ArrayList<>();
    private static Card[][] market = new Card[3][4];
    private static ArrayList<NobleTile> nobles;
    private static Scanner sc = new Scanner(System.in); // can like that??
    private static long seed;

    public static void main(String[] args) {

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
            System.out.println(winningPlayers.get(i).getName());
        }
        sc.close();

    }

    public Game(int playerNumber) {
        this(playerNumber, (new Random()).nextLong());
    }

    public Game(int playerNumber, long seed) {
        Configuration.load();

        this.playerNumber = playerNumber;
        this.seed = seed;
        players = new ArrayList<>(playerNumber);

        int startingGems = Configuration.getStartingGems(playerNumber);
        for (Gem gem : Gem.values()) {
            bank.put(gem, startingGems);
        }

        for (int i = 0; i < 3; i++) {
            decks.add(new Deck(Configuration.getDeck(i + 1)));
            decks.get(i).shuffleDeck(seed);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                market[i][j] = decks.get(i).draw();
            }
        }

        nobles = new ArrayList<NobleTile>(playerNumber + 1);
        Deck<NobleTile> nobleTileDeck = Configuration.getNobleTiles();
        nobleTileDeck.shuffleDeck(seed);
        for (int i = 0; i < playerNumber + 1; i++) {
            nobles.add(nobleTileDeck.draw());
        }

        setPlayerArray(playerNumber);
    }

    public long getSeed() {
        return seed;
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

    public static void doPlayerTurn(Player player) {

        boolean turnDone = false;
        while (!turnDone) {
            // show board every turn? might affect the choice they make
            printBoard();
            turnOptionDisplay();

            switch (enterNumber(1, 4)) {
                case 1:
                    turnDone = drawToken(player);
                    break;
                case 2:
                    turnDone = reserveCard(player);
                    break;
                case 3:
                    turnDone = buyCard(player);
                    List<NobleTile> visitingNobles = visitingNobles(player);
                    if (visitingNobles.size() > 1) {
                        System.out.print("Please select a noble: ");
                        int choice = enterNumber(1, visitingNobles.size());
                        // display choices
                        System.out.println("todo: display visiting nobles");
                        NobleTile noble = visitingNobles.get(choice - 1); // choice 1 corresponds to idx 0
                        player.addNobleTile(noble);
                        nobles.remove(noble);
                    } else if (visitingNobles.size() == 1) {
                        player.addNobleTile(visitingNobles.get(0));
                        nobles.remove(visitingNobles.get(0));
                    }
                    break;
                case 4:
                //skip turn????
            }
        }
    }

    public static boolean hitWinCondition(Player p) {
        return p.getPoints() == 15;
    }

    public static List<Player> getWinner() {
        Collections.sort(players);
        List<Player> winningPlayers = new ArrayList<>();
        winningPlayers.add(players.get(0));

        int idx = 0;
        while (idx + 1 <= players.size() - 1) {
            Player p1 = players.get(idx);
            Player p2 = players.get(idx + 1);

            if (p1.compareTo(p2) == 0) {
                winningPlayers.add(p2);
                idx++;
            } else {
                break;
            }

        }
        return winningPlayers;

    }

    public static List<NobleTile> visitingNobles(Player p) {
        List<NobleTile> result = new ArrayList<>();
        HashMap<Gem, Integer> playerTokens = p.getTokens();

        for (NobleTile n : nobles) {
            boolean qualify = true;
            HashMap< Gem, Integer> nobleTokens = n.getTokens();
            for (Map.Entry<Gem, Integer> entry : nobleTokens.entrySet()) {
                if (playerTokens.get(entry.getKey()) < entry.getValue()) {
                    qualify = false;
                    break;
                }
            }
            if (qualify) {
                result.add(n);
            }

        }
        return result;
    }

    public static boolean reserveCard(Player p) {

        

        int[] choice = Utility.getPositionOnBoard(sc);
        // convert choice to corresponding card
        Card card = market[choice[0] - 1][choice[1] - 1];

        boolean success = p.reserveCard(card);
        if (!success) {
            System.out.println("error: unable to reserve Card. Hand Limit reached");
            return false;
        }

        // add to player
        // add gold if gold in bank
        if (bank.get(Gem.Gold) > 0) {
            p.addToken(Gem.Gold, 1);
            bank.replace(Gem.Gold, bank.get(Gem.Gold) - 1);
        }
        // remove from market
        market[choice[0] - 1][choice[1] - 1] = decks.get(choice[0] - 1).draw();

        return true;
    }

    public static boolean buyCard(Player p) {

        while (true){
            System.out.println();
            System.out.println("1. Buy from market"); 
            System.out.println("2. Buy from reserve"); 
            System.out.println("0. Cancel"); 
            System.out.println();
        
            int choice = Utility.askForNum(sc, 0, 2, "Enter your choice: "); 

            if (choice == 0){
                return false; 
            }

            if (choice == 1){
                // buy from market 
                int[] pos = Utility.getPositionOnBoard(sc); 
                Card card = market[pos[0] - 1][pos[1] - 1]; 

                if (card == null){
                    System.out.println("No card at that position"); 
                    continue; 
                }

                Map<Gem, Integer> pBefore = new HashMap<>(p.getTokens()); 
                boolean success = p.buyCard(card, sc); 
                if (!success){
                    System.out.println("Unable to buy that card.");
                    continue;  
                }

                Map <Gem, Integer> pAfter = p.getTokens(); 

                for (Gem g : Gem.values()) {
                    int diff =  pBefore.get(g) - pAfter.get(g);
                    bank.replace(g, diff + bank.get(g));
                }

                // remove from market
                market[pos[0] - 1][pos[1] - 1] = decks.get(pos[0] - 1).draw();
                return true;
            }

            if (choice == 2){
                // buy from reserve 
                List<Card> hand = p.getReserveHand(); 
                if (hand.isEmpty()){
                    System.out.println("You have no reserved cards.");
                    continue; 
                }

                System.out.println("Your reserved cards:");
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ". " + hand.get(i));
                }

                int idx = Utility.askForNum(sc, 1, hand.size(), "Enter card number: ") - 1;
                Card card = hand.get(idx);

                Map<Gem, Integer> pBefore = new HashMap<>(p.getTokens());
                boolean success = p.buyCard(card, sc);
                if (!success) {
                    System.out.println("Unable to buy that card.");
                    continue;
                }

                Map<Gem, Integer> pAfter = p.getTokens();

                for (Gem g : Gem.values()) {
                    int diff = pBefore.get(g) - pAfter.get(g);
                    bank.replace(g, diff + bank.get(g));
                }

                p.removeReserveCard(idx);
                return true;
            }
        }
    }

    // drawToken function 
    public static boolean drawToken(Player currentPlayer) {

        boolean validAction = false;

        while (!validAction) {
            System.out.println(); 
            System.out.println("Choose token option: ");
            System.out.println(); 
            System.out.println("1. Take 3 different tokens");
            System.out.println("2. Take 2 same tokens");
            System.out.println("0. Cancel");

            int choice = Utility.askForNum(sc, 0, 2, "Enter your choice: ");
            // int choice = enterNumber(0, 2);

            if (choice == 0) {
                return false;
            }

            // add token - option 1: 3 different tokens 
            if (choice == 1) {

                Set<Gem> chosen = pickThreeDifferentGems();

                if (chosen.isEmpty()) {
                    continue; // user cancelled 
                }

                for (Gem g : chosen) {
                    bank.replace(g, bank.get(g) - 1);
                    currentPlayer.addToken(g, 1);
                }

                validAction = true;

            } else if (choice == 2) {
                Gem g = pickTwoSameGem();

                if (g == null) {
                    continue; // user cancelled 
                }

                bank.replace(g, bank.get(g) - 2);
                currentPlayer.addToken(g, 2);

                validAction = true;
            }
        }

        // checksize
        // if exceed, prompt user to return tokens 
        int totalTokens = 0;
        for (Gem g : Gem.values()) {
            totalTokens += currentPlayer.getTokens().get(g);
        }

        while (totalTokens > 10) {

            currentPlayer.displayTokens();
            System.out.print("You have more than 10 tokens. Return 1 token:");
            String input = sc.nextLine().trim();

            if (input.length() != 1){
                System.out.println("Invalid gem."); 
                continue; 
            }

            char c = Character.toUpperCase(input.charAt(0));
            Gem g = Utility.fromCharToGem(c);

            if (g == null && c == 'G'){
                g = Gem.Gold; 
            }

            if (g == null){
                System.out.println("Invalid gem."); 
                continue; 
            }

            if (currentPlayer.getTokens().get(g) > 0) {
                currentPlayer.removeToken(g, 1);
                bank.replace(g, bank.get(g) + 1);
                totalTokens--;
            } else {
                System.out.println("You don't have that token.");
            }
        }

        return true;
    }

    private static Set<Gem> pickThreeDifferentGems() {
        Set<Gem> chosen = new HashSet<>();

        while (chosen.size() < 3) {
            
            System.out.print("Enter gem (D/R/S/E/O) or 'cancel': ");
            String gemInput = sc.nextLine().trim();

            if (gemInput.isEmpty()) {
                continue;
            }

            if (gemInput.equalsIgnoreCase("cancel")) {
                return new HashSet<>(); // return empty set 
            }

            if (gemInput.length() != 1){
                System.out.println("Invalid gem."); 
                continue; 
            }

            char c = Character.toUpperCase(gemInput.charAt(0));
            Gem g = Utility.fromCharToGem(c);

            if (g == null) {                          
                System.out.println("Invalid gem.");
                continue;
            }

            if (g == Gem.Gold) {
                System.out.println("Unable to take gold this way.");
                continue;
            }

            if (bank.get(g) <= 0) {
                System.out.println("Bank does not have this gem.");
                continue;
            }

            if (chosen.contains(g)) {
                System.out.println("Already chosen.");
                continue;
            }

            chosen.add(g);
        } 

        return chosen;
    }

    private static Gem pickTwoSameGem() {
        while (true) {
            System.out.print("Enter gem (D/R/S/E/O) or 'cancel':");
            String gemInput = sc.nextLine().trim();

            if (gemInput.isEmpty()) {
            continue;
            }

            if (gemInput.equalsIgnoreCase("cancel")) {
                return null;  // handled in drawToken
            }

            if (gemInput.length() != 1){
                System.out.println("Invalid gem."); 
                continue; 
            }
            
            char c = Character.toUpperCase(gemInput.charAt(0));
            Gem g = Utility.fromCharToGem(c);

            if (g == null) {
                System.out.println("Invalid gem.");
                continue;
            }

            if (g == Gem.Gold) {                      
                System.out.println("Unable to take gold this way.");
                continue;
            }

            if (bank.get(g) < 4) {
                System.out.println("Need at least 4 in bank to take 2.");
                continue;
            }

            return g;  // valid gem found
        }
    }

    public static void turnOptionDisplay() {
        System.out.println("1. Draw tokens");
        System.out.println("2. Reserve a card");
        System.out.println("3. Buy a card");
        System.out.println();
        System.out.print("Please enter your choice:");
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
                System.out.printf("%d.%d %s\n", i, j, market[i - 1][j - 1].toString());
            }
        }
        System.out.printf("------------------------------------------------------------------\n");

        printNobles();

        System.out.printf("------------------------------------------------------------------\n\n");
    }

    public static void printNobles() {

        System.out.printf("<NOBLE TILES>\n");
        for (int i = 0; i < nobles.size(); i++) {
            System.out.printf("%s\n", nobles.get(i));
        }
    }

    // Overloaded printPlayer method 1
    public static void printPlayer(String name) {
        // incomplete
    }

    // Overloaded printPlayer method 2
    public static void printPlayer(int no) {

    }

    public static void printAllPlayers() {

    }

    public static void printCommandList() {

    }

}
