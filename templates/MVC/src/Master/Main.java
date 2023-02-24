package Master;

import java.time.Duration;
import java.util.Arrays;

import javax.management.timer.TimerNotification;

import Request.Request;
import Request.RequestCode;
import TimeProfiler.RunnableFunction;
import TimeProfiler.TimeProfiler;
import TimeProfiler.TimeResult;
import View.Section;
import View.View;
import java.awt.*;

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

        TimeProfiler tp = new TimeProfiler(500);
        int a = 0;
        RunnableFunction<Integer> fn = new RunnableFunction<>(params -> {
            int b = 0;
            for (int i = 0; i < params[0]; i++) {
                for (int index = 0; index < params[0]; index++) {
                    b += 1;
                }
            }
            params[1] = b;
        }, 1000, a);

        // Arrays.stream(TimeProfiler.batchTimeIt(new Runnable[] { this::fn, this::fn,
        // this::fn }, 10))
        // .map(x -> x.mean(Duration::toMillis))
        // .map(Object::toString)
        // .forEach(System.out::println);

        // TimeResult a = TimeProfiler.batchTimeIt(this::fn, 10);
        // System.out.println(a.toString(Duration::toMillis));
        // System.out.println(a.mode(Duration::toMillis));
        System.out.println(a);
        fn.run();
        System.out.println(a);

        System.out.println("interference: " + tp.getInterferenceRatio());
        double interferencedTimeRatio = tp.getInterferenceRatio();
        System.out.println("lambda: " + tp.batchTimeIt(fn, 50).mean(Duration::toNanos) * interferencedTimeRatio);

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

    private void testView() {
        Section panel = new Section();
        String columnLabels[] = { "A", "B", "C", "D", "E", "F" };
        int values[] = { 350, 690, 510, 570, 180, 504 };
        Color colors[] = { Color.RED, Color.YELLOW, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.CYAN };
        panel.createHistogramChart(columnLabels, values, colors);
        View v = new View();
        v.initConfig(null); // "config.txt"
        v.addSection(panel);
        v.start();
    }

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        // (new Main()).master();
        (new Main()).testView();
    }

}
