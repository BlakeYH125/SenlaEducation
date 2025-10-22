public class Room {
    private int number;
    private double price;
    private String status;
    private Guest guest;

    public Room(int number, double price, String status) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status.toLowerCase();
    }

    @Override
    public String toString() {
        return "Комната номер " + number + ", стоимость " + price + ", " + status;
    }
}
