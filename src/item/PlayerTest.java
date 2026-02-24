package item;


public class PlayerTest {

    public static void main(String[] args) {

        Player p1 = new Player("A");
        Player p2 = new Player("B");

        System.out.println(p1);
        System.out.println(p2);

        p1.addPoints(15);
        p2.addPoints(15);

        p1.addProduction(Gem.Diamond);
        p1.addProduction(Gem.Diamond);
        p2.addProduction(Gem.Diamond);

        System.out.println(p1);
        System.out.println(p2);

        System.out.println(p1.compareTo(p2));


    }
}
