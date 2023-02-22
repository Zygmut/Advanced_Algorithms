package Master;

import java.time.Duration;
import java.util.Arrays;

import Request.Request;
import Request.RequestCode;
import TimeProfiler.TimeProfiler;
import TimeProfiler.TimeResult;

public class Main {

    public void fn() {

        for (int i = 0; i < 10000; i++) {
            for (int index = 0; index < 100; index++) {
                System.out.print("");
            }
        }
        return;
    }

    public void master() {
        Arrays.stream(TimeProfiler.batchTimeIt(new Runnable[] { this::fn, this::fn, this::fn }, 10))
                .map(x -> x.mean(Duration::toMillis))
                .map(Object::toString)
                .forEach(System.out::println);

        System.out.println(new Request(RequestCode.None, this, this));

    }

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        (new Main()).master();
    }
}
