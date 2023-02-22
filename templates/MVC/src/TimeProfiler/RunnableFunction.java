package TimeProfiler;

import java.util.function.Consumer;

public class RunnableFunction<T> implements Runnable {
    private final Consumer<T[]> runnable;
    private final T[] args;

    @SafeVarargs
    public RunnableFunction(Consumer<T[]> runnable, T... args) {
        this.runnable = runnable;
        this.args = args;
    }

    @Override
    public void run() {
        runnable.accept(args);
    }

}
