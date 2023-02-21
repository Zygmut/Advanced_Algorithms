import java.time.Duration;

public class TimeComplexity {

    public static Duration timeIt(Runnable fn) {
        long startingNanoSeconds = System.nanoTime();
        fn.run();
        long endingNanoSeconds = System.nanoTime();
        return Duration.ofNanos((endingNanoSeconds - startingNanoSeconds));
    }
}
