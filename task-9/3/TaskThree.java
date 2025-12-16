import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskThree {
    static int bufferSize = 5;
    static List<Integer> currentBuffer = new ArrayList<>();
    static Random random = new Random();

    public static void main(String[] args) {
        Object monitor = new Object();
        Thread producer = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    synchronized (monitor) {
                        while (currentBuffer.size() == bufferSize) {
                            try {
                                monitor.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        int number = random.nextInt(100);
                        currentBuffer.add(number);
                        System.out.println("Добавлено число " + number);
                        monitor.notifyAll();
                    }
                }
            }
        });
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (monitor) {
                        while (currentBuffer.size() == 0) {
                            try {
                                monitor.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        System.out.println("Удалено число " + currentBuffer.get(0));
                        currentBuffer.remove(0);
                        monitor.notifyAll();
                    }
                }
            }
        });
        producer.start();
        consumer.start();
    }
}
