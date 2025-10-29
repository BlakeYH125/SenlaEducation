public abstract class Flower {
    protected String color;
    protected int cost;
    protected int length;

    public Flower(String color, int cost, int length) {
        this.color = color;
        this.cost = cost;
        this.length = length;
    }

    public String getColor() {
        return color;
    }

    public int getCost() {
        return cost;
    }

    public int getLength() {
        return length;
    }
}
