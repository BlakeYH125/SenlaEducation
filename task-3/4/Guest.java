public class Guest extends Person{
    private Room rentRoom;

    public Guest(String fullName, int age) {
        this.fullName = fullName;
        this.age = age;
    }

    public void setRentRoom(Room room) {
        this.rentRoom = room;
    }

    @Override
    public String toString() {
        return fullName + " " + age + " лет.";
    }
}
