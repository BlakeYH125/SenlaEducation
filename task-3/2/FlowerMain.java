public class FlowerMain {
    public static void main(String[] args) {

        Flower whiteRose = new Rose("white", 125, 40);
        Flower redRose = new Rose("red", 150, 50);
        Flower pinkPion = new Pion("pink", 500, 75);
        Flower yellowTulip = new Tulip("yellow", 75, 50);
        Flower redTulip = new Tulip("red", 80, 50);
        Flower whiteChrysanthemum = new Chrysanthemum("white", 100, 75);

        Bouquet bouquet = new Bouquet();
        bouquet.add(whiteRose);
        bouquet.add(whiteRose);
        bouquet.add(whiteRose);
        bouquet.add(whiteChrysanthemum);
        bouquet.add(whiteChrysanthemum);
        bouquet.add(whiteChrysanthemum);
        bouquet.add(pinkPion);
        bouquet.add(pinkPion);
        bouquet.add(pinkPion);

        System.out.println("Стоимость букета: " + bouquet.getTotalCost());
    }
}
