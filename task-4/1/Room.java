import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Room{
    private int number;
    private double price;
    private int capacity;
    private int stars;
    private Status status;
    private Guest guest;
    private Date releasedIn;
    private List<Guest> previousGuests;

    public Room(int number, double price, Status status, int capacity, int stars) {
        this.number = number;
        this.price = price;
        this.capacity = capacity;
        this.stars = stars;
        this.status = status;
        previousGuests = new ArrayList<>();
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

    public int getCapacity() {
        return capacity;
    }

    public int getStars() {
        return stars;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getReleasedIn() {
        return releasedIn;
    }

    public void setReleasedIn(Date releasedIn) {
        this.releasedIn = releasedIn;
    }

    public void addToPrevGuestsList() {
        previousGuests.add(guest);
        guest = null;
    }

    public List<Guest> getPrevGuests() {
        return previousGuests;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        if (releasedIn == null) {
            return "Комната номер " + number + ", стоимость: " + price + ", вместимость: " + capacity + ", звезды: "
                    + stars + ", " + status.toString();

        }
        return "Комната номер " + number + ", стоимость: " + price + ", вместимость: " + capacity + ", звезды: "
                + stars + ", " + status.toString() + ", освободится " + sdf.format(releasedIn);
    }

}
