import java.util.Date;

public class Time implements Runnable{
    private int interval;

    public Time(int interval) {
        this.interval = interval;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println(new Date(System.currentTimeMillis()));
            try {
                Thread.sleep(interval * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
