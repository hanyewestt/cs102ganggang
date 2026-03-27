package item;

import java.util.*;

public class PlayerTest {

    private List<Player> players = new ArrayList<>();

    public List<Player> getWinner() {
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

    public PlayerTest() {
        Player p1 = new Player("A", 1);
        Player p2 = new Player("B", 2);
        Player p3 = new Player("C", 3);

        p1.addPoints(15);
        p2.addPoints(15);
        p3.addPoints(14);

        p1.addProduction(Gem.Diamond);
        p2.addProduction(Gem.Diamond);

        players.add(p1);
        players.add(p2);
        players.add(p3);
    }

    public static void main(String[] args) {
        PlayerTest p1 = new PlayerTest();
        List<Player> winners = p1.getWinner();

        for (int idx = 0; idx < winners.size(); idx++) {
            System.out.println(winners.get(idx));
        }

    }
}
