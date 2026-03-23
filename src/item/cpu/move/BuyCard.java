package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;

public class BuyCard extends Move {

    int row;
    int column;

    public void doMove(Game splendor, CPUPlayer cpu) {
        cpu.buyCard(splendor.get());
    }
}
