public class HotelMain {
    public static void main(String[] args) {
        Administrator administrator = new Administrator();
        Console console = new Console();
        MenuController controller = new MenuController(administrator, console);
        controller.run();
    }
}