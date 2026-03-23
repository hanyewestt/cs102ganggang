package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;

public class BuyCard extends Move {

    private int type; // Represents deck no, if 0 it represents reserve hand
    private int idx;

    public BuyCard(int type, int column, CPUPlayer cpu) {
        this.type = type;
        this.column = column;


    }
    
    public void doMove(Game splendor, CPUPlayer cpu) {
        cpu.buyCard(splendor.get());
    }
}
