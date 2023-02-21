import java.sql.Time;

import mesurament.Mesurament;

public class Main {

    public void fn() {
        return;
    }

    public void master() {
        // Mesurament.mesura();
        System.out.println(TimeComplexity.timeIt(this::fn).toNanos());
    }

    public static void main(String[] args) throws Exception {
        (new Main()).master();
    }
}
