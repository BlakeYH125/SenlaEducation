import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class Room implements Priceable {
    private int number;
    private double price;
    private int capacity;
    private int stars;
    private Status status;
    private List<Guest> guests;
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

    public List<Guest> getGuests() {
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    @Override
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
        for (Guest guest : guests) {
            previousGuests.add(guest);
        }
        guests = null;
    }

    public List<Guest> getPrevGuests() {
        return previousGuests;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        if (releasedIn == null) {
            return "Номер " + number + ", стоимость: " + price + ", вместимость: " + capacity + ", звезды: "
                    + stars + ", " + status.toString();

        }
        return "Номер " + number + ", стоимость: " + price + ", вместимость: " + capacity + ", звезды: "
                + stars + ", " + status.toString() + ", освободится " + sdf.format(releasedIn);
    }


}
