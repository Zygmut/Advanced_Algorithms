package Master;

import java.time.Duration;
import java.util.Arrays;

import javax.management.timer.TimerNotification;

import Request.Request;
import Request.RequestCode;
import TimeProfiler.RunnableFunction;
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
        RunnableFunction<Integer> fn = new RunnableFunction<>(params -> {
            int a = 0;
            for (int i = 0; i < params[0]; i++) {
                for (int index = 0; index < params[0]; index++) {
                    a = a + (a - 1);
                }
            }
        }, 1000);

        // Arrays.stream(TimeProfiler.batchTimeIt(new Runnable[] { this::fn, this::fn,
        // this::fn }, 10))
        // .map(x -> x.mean(Duration::toMillis))
        // .map(Object::toString)
        // .forEach(System.out::println);

        // TimeResult a = TimeProfiler.batchTimeIt(this::fn, 10);
        // System.out.println(a.toString(Duration::toMillis));
        // System.out.println(a.mode(Duration::toMillis));
        double interferencedTimeRatio = TimeProfiler.getInterferenceRatio(1000);
        System.out.println(interferencedTimeRatio);

        System.out.println("lambda: " + TimeProfiler.batchTimeIt(fn, 50).mean(Duration::toNanos)*interferencedTimeRatio);

        long[] functionDurations = new long[50];
        for (int i = 0; i < 50; i++) {
            long startingNanoSeconds = System.nanoTime();
            fn(1000);
            long endingNanoSeconds = System.nanoTime();
            functionDurations[i] = endingNanoSeconds - startingNanoSeconds;
        }

        System.out.println("native: "
                + new TimeResult(Arrays.stream(functionDurations).mapToObj(Duration::ofNanos).toArray(Duration[]::new))
                        .mean(Duration::toNanos));

    }

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        (new Main()).master();
    }
}
