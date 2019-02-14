import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorsTest {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(2, 4,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());

        executorService.submit(newThread());
        executorService.submit(newThread());
        executorService.submit(newThread());
        executorService.submit(newThread());
        executorService.submit(newThread());

        executorService.shutdown();

        System.out.println("over");

    }

    public static Thread newThread() {
        Thread thread = new Thread(() -> {
            sleep();
            System.out.println(Thread.currentThread().getName() + ".. over");
        });
        return thread;
    }

    public static void sleep() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
        }
    }
}
