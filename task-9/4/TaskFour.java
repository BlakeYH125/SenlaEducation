public class TaskFour {
    public static void main(String[] args) throws InterruptedException {
        int interval = 3;
        Thread thread = new Thread(new Time(interval));
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(10000);
    }
}
