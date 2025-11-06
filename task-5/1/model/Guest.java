import java.text.SimpleDateFormat;
import java.util.Date;

public class Guest extends Person {
    private Room rentRoom;
    private Date arriveDate;
    private Date departureDate;

    public Guest(String fullName, int age) {
        this.fullName = fullName;
        this.age = age;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public void setRentRoom(Room room) {
        this.rentRoom = room;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (rentRoom != null) {
            return String.format(fullName + " " + age + " лет, комната " + rentRoom.getNumber() + ", " + sdf.format(arriveDate) + "-" + sdf.format(departureDate));
        } else {
            return String.format(fullName + " " + age + " лет, " + sdf.format(arriveDate) + "-" + sdf.format(departureDate));
        }
    }
}
