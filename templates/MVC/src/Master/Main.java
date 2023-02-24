package Master;

import java.time.Duration;
import java.util.Arrays;

import TimeProfiler.TimeProfiler;
import TimeProfiler.TimeResult;

public class Main {

    public void fn(int n) {
        int a = 0;
        for (int i = 0; i < n; i++) {
            for (int index = 0; index < n; index++) {
                a = a + (a - 1);
            }
        }
        return;
    }

    public void master() {

        TimeProfiler tp = new TimeProfiler(1000);

        // Arrays.stream(TimeProfiler.batchTimeIt(new Runnable[] { this::fn, this::fn,
        // this::fn }, 10))
        // .map(x -> x.mean(Duration::toMillis))
        // .map(Object::toString)
        // .forEach(System.out::println);

        // TimeResult a = TimeProfiler.batchTimeIt(this::fn, 10);
        // System.out.println(a.toString(Duration::toMillis));
        // System.out.println(a.mode(Duration::toMillis));

        for (int i = 0; i < 5; i++) {
            final int n = i;
            System.out.println("lambda: " + tp.batchTimeIt(() -> fn(n), 50).mean(Duration::toNanos));

            long[] functionDurations = new long[50];
            for (int j = 0; j < 50; j++) {
                long startingNanoSeconds = System.nanoTime();
                fn(n);
                long endingNanoSeconds = System.nanoTime();
                functionDurations[j] = endingNanoSeconds - startingNanoSeconds;
            }

            System.out.println("native: "
                    + new TimeResult(
                            Arrays.stream(functionDurations).mapToObj(Duration::ofNanos).toArray(Duration[]::new))
                            .mean(Duration::toNanos));
        }

    }

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        (new Main()).master();
    }
}
