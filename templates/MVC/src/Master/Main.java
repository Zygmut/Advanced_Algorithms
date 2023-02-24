package Master;

import TimeProfiler.TimeProfiler;
import View.Section;
import View.View;
import java.awt.*;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;

public class Main {

    public <T extends Number> Optional<double[]> declarativeEscalarProduct(T[] vec1, T[] vec2) {
        if (vec1.length != vec2.length) {
            return null;
        }

        double[] result = new double[vec1.length];
        for (int i = 0; i < vec1.length; i++) {
            result[i] = 0.0;
            for (T value : vec2) {
                result[i] += vec1[i].doubleValue() * value.doubleValue();
            }
        }
        return Optional.of(result);
    }

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
        // Arrays.stream(TimeProfiler.batchTimeIt(new Runnable[] { this::fn, this::fn,
        // this::fn }, 10))
        // .map(x -> x.mean(Duration::toMillis))
        // .map(Object::toString)
        // .forEach(System.out::println);

        // TimeResult a = TimeProfiler.batchTimeIt(this::fn, 10);
        // System.out.println(a.toString(Duration::toMillis));
        // System.out.println(a.mode(Duration::toMillis));

        Random rng = new Random();
        for (int i = 1000; i < 1500; i++) {
            Integer[] vector = rng.ints(1, 100).limit(i).mapToObj(Integer::valueOf).toArray(Integer[]::new);
            //System.out.println(TimeProfiler.batchTimeIt(() -> declarativeEscalarProduct(vector, vector) , 100).toString(Duration::toNanos));
            System.out.println(TimeProfiler.batchTimeIt(() -> declarativeEscalarProduct(vector, vector) , 1000).mean(Duration::toMillis));

        }
    }

    private void testView() {
        Section panel = new Section();
        String columnLabels[] = { "A", "B", "C", "D", "E", "F" };
        int values[] = { 350, 690, 510, 570, 180, 504 };
        Color colors[] = {
                Color.RED,
                Color.YELLOW,
                Color.BLUE,
                Color.ORANGE,
                Color.MAGENTA,
                Color.CYAN,
        };
        panel.createHistogramChart(columnLabels, values, colors);
        View v = new View();
        v.initConfig(".\\MVC\\config.txt"); // "config.txt"
        v.addSection(panel);
        v.start();
    }

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        //(new Main()).master();
        (new Main()).testView();
    }
}
