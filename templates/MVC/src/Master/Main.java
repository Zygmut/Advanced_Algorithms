package Master;

import java.time.Duration;
import java.util.Arrays;

import Request.Request;
import Request.RequestCode;
import Utils.TimeProfiler;
import mesurament.Mesurament;

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
        MVC mvc = new MVC();
        Arrays.stream(TimeProfiler.batchTimeIt(this::fn, 10))
                .mapToLong(Duration::toMillis)
                .forEach(System.out::println);

        System.out.println(new Request(RequestCode.None, this, this));

    }


    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        (new Main()).master();
    }
}
