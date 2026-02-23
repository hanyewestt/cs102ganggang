package item;

import java.util.*;

public class Deck<T> {

    private ArrayList<T> deck;

    /**
     * Constructor that makes an empty deck.
     *
     */
    public Deck() {
        deck = new ArrayList<T>();
    }

    /**
     * Shuffles the deck.
     */
    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    /**
     * Draws an object from the top of the deck, removing it from the deck and
     * returning the object. Returns null if deck is empty.
     *
     * @return The drawn object, null if the deck is empty.
     */
    public T draw() {
        int deckSize = deck.size();

        if (deckSize == 0) {
            return null;
        }

        return deck.remove(0);
    }

    /**
     * Adds an object to the top of the object deck.
     *
     * @param newObject The object to be added.
     */
    public void addToDeck(T newObject) {
        deck.add(newObject);
    }
}
