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

    // drawToken function 
    public boolean drawToken(Player currentPlayer){ 

        boolean validAction = false; 

        while (!validAction){
            System.out.println("Choose token option: "); 
            System.out.println("1. Take 3 different tokens"); 
            System.out.println("2. Take 2 same tokens"); 
            System.out.println("0. Cancel")

            int choice = enterNumber(0, 2); 

            if (choice == 0){
                return false; 
            }

            // add token - option 1: 3 different tokens 
            if (choice == 1){

                Set<Gem> chosen = new HashSet<>(); 

                while (chosen.size() < 3){
                    System.out.println("Enter gem (Diamond, Ruby, Sapphire, Emerald, Onyx): "); 
                    String gemInput = sc.nextLine(); 

                    try {
                        Gem g = Gem.valueOf(gemInput); 

                        if (g == Gem.Gold){
                            System.out.println("Unable to take gold this way."); 
                            continue; 
                        }

                        if (bank.get(g) <= 0){
                            System.out.println("Bank does not have this gem."); 
                            continue; 
                        }

                        if (chosen.contains(g)){
                            System.out.println("Already chosen"); 
                            continue; 
                        }

                        chosen.add(g); 

                    } catch (IllegalArgumentException e){
                        System.out.println("Invalid gem."); 
                    }
                }

                for (Gem g : chosen){
                    bank.put(g, bank.get(g) - 1); 
                    currentPlayer.addToken(g, 1); 
                }

                validAction = true; 

            } else if (choice == 2) { 
                System.out.println("Enter gem (Diamond, Ruby, Sapphire, Emerald, Onyx):"); 
                String gemInput = sc.nextLine(); 

                try {
                    Gem g = Gem.valueOf(gemInput); 

                    if (g == Gem.Gold){
                        System.out.println("Unable to take gold this way."); 
                        continue; 
                    }

                    if (bank.get(g) < 4){
                        System.out.println("Need at least 4 in bank to take 2."); 
                        continue; 
                    }

                    bank.put(g, bank.get(g) - 2); 
                    currentPlayer.addToken(g, 2); 

                    validAction = true; 

                } catch (IllegalArgumentException e){
                    System.out.println("Invalid gem"); 
                } 
            
            } else {
                System.out.println("Invalid choice"); 
            }
        }


        // checksize
        // if exceed, prompt user to return tokens 
        int totalTokens = 0; 
        for (Gem g : Gem.values()){
            totalTokens += currentPlayer.getTokens().get(g); 
        }

        while (totalTokens > 10){
            System.out.println("You have more than 10 tokens. Return 1 token:"); 
            String input = sc.nextLine(); 

            try {
                Gem g = Gem.valueOf(input); 

                if (currentPlayer.getTokens().get(g) > 0){
                    currentPlayer.removeToken(g, 1); 
                    bank.put(g, bank.get(g) + 1); 
                    totalTokens--; 
                } else {
                    System.out.println("You don't have that token."); 
                }

            } catch (IllegalArgumentException e){
                System.out.println("Invalid gem."); 
            }
        }

        return true; 
    }
}
