import app.*;
import config.*;
import item.*;

public class Test {
    // for code testing purposes

    public static void main(String[] args) {
        // Card card1 = new Card('D', 3, 2, 0, 2, 3, 0);

        // System.out.println(card1.toString());

        Configuration.load();

        System.out.println(Configuration.getNobleTilePoints());
        System.out.println(Configuration.getPointsToWin());

        Deck<Card> deck1 = Configuration.getDeck(1);

        for (int i = 0; i < 10; i++) {
            System.out.println(deck1.draw());
        }
    }
}