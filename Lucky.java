import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Lucky {
    static final int threadsNumber = 3;
    static int x = 0;
    static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadsNumber, threadsNumber,
                1, TimeUnit.SECONDS, new LinkedBlockingQueue(threadsNumber));

        AtomicInteger inProgress = new AtomicInteger(0);
        long begin = System.nanoTime();

        while (x < 999999) {
            if (inProgress.get() >= executor.getMaximumPoolSize()) {
                continue;
            }

            task(executor, inProgress);
        }

        long end = System.nanoTime();
        System.out.println("Total: " + count.get());
        System.out.printf("Milliseconds: %d", (end - begin) / 1000000);
    }

    private static void task(ThreadPoolExecutor executor, AtomicInteger inProgress) {
        inProgress.incrementAndGet();

        final int value = x++;
        executor.submit(() -> {
            if ((value % 10) + (value / 10) % 10 + (value / 100) % 10 == (value / 1000)
                    % 10 + (value / 10000) % 10 + (value / 100000) % 10) {
                System.out.println(Thread.currentThread().getName() + ":" + value);
                count.incrementAndGet();
            }
            inProgress.decrementAndGet();
        });
    }
}
