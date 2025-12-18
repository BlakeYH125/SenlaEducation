public class TaskTwo {
    static boolean isThreadOneTurn = true;
    public static void main(String[] args) {
        Object monitor = new Object();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (monitor) {
                    while (true) {
                        while (!isThreadOneTurn) {
                            try {
                                monitor.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        System.out.println(Thread.currentThread().getName());
                        isThreadOneTurn = false;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        monitor.notify();
                    }
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (monitor) {
                    while (true) {
                        while (isThreadOneTurn) {
                            try {
                                monitor.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        System.out.println(Thread.currentThread().getName());
                        isThreadOneTurn = true;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        monitor.notify();
                    }
                }
            }
        });
        thread1.start();
        thread2.start();
    }
}
