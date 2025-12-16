public class TaskOne {
    public static void main(String[] args) throws InterruptedException {
        Object monitor = new Object();
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (monitor) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    thread2.join();
                    Thread.sleep(3000);
                    synchronized (monitor) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        System.out.println(thread1.getState());
        thread1.start();
        thread2.start();
        thread3.start();
        System.out.println(thread1.getState());
        Thread.sleep(50);
        System.out.println(thread1.getState());
        Thread.sleep(3000);
        System.out.println(thread1.getState());
        Thread.sleep(3500);
        System.out.println(thread1.getState());
        Thread.sleep(5000);
        System.out.println(thread1.getState());
    }
}
