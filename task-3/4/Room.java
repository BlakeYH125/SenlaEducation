public class Room {
    private int number;
    private double price;
    private Status status;
    private Guest guest;

    public Room(int number, double price, Status status) {
        this.number = number;
        this.price = price;
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Комната номер " + number + ", стоимость " + price + ", " + status.toString();
    }
}
