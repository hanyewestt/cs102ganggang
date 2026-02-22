import java.util.*;

public class Game {
    private int playerNumber;
    private ArrayList<Player> players;
    private HashMap<Gem, Integer> bank;
    private Deck[] decks;
    private Card[][] market;
    private NobleTile[] nobles;

    // idk
    public Game(int playerNumber) {
        this.playerNumber = playerNumber;
        this.decks = new Deck[3];
        this.market = new Card[3][4];
        this.nobles = new NobleTile[playerNumber + 1];

        bank.put( , 7 - (4 - playerNumber));

    }

    public void cardSetup() {

    }

    // HELP manually set array accoring the the bday shit or just print out enter
    // player with the lowest bday
    public void setPlayerArray(int playerNumber) {
        for (int i = 0; i < playerNumber; i++) {
            // scan for name
            // scan for birthday
        }
        // comparator

    }

    public static void main(String[] args) {
        // scan for player num

        Game game = new Game(playerNumber);

    }
}