package item;

import java.util.*;

public class Deck<T> {

    private ArrayList<T> deck;

    /**
     * Constructor that makes an empty deck.
     */
    public Deck() {
        deck = new ArrayList<>();
    }

    /**
     * Constructor that takes in a previously constructed deck.
     * Returns a deep copy of the given deck.
     * If passed a null reference, constructs an empty deck.
     * 
     * @param orig The deck to be copied.
     */
    public Deck(Deck<T> orig) {
        this();
        if (orig == null) {
            return;
        }
        
        for (T object : orig.getDeck()) {
            deck.add(object);
        }
    }

    /**
     * Getter to retrieve the ArrayList of the deck.
     * 
     * @return The ArrayList that represents the deck.
     */

    public ArrayList<T> getDeck() {
        return deck;
    }

    /**
     * Shuffles the deck.
     * 
     * @param seed Random seed used for shuffle
     */
    public void shuffleDeck(long seed) {
        Collections.shuffle(deck, new Random(seed));
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
