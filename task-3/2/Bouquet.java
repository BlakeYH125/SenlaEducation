import java.util.ArrayList;
import java.util.List;

public class Bouquet {
    private List<Flower> bouquet = new ArrayList<>();

    public void add(Flower flower) {
        bouquet.add(flower);
    }

    public int getTotalCost() {
        int totalCost = 0;
        for (Flower flower : bouquet) {
            totalCost += flower.getCost();
        }
        return totalCost;
    }
}
