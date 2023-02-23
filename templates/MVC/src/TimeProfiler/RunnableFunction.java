package TimeProfiler;

import java.util.function.Consumer;

/**
 * Encloses a lambda function with its parameters. i.e
 * <pre>
 * {@code
 * RunnableFunction<Integer> fn = new RunnableFunction<>(
 *  params -> {
 *      for(int i = 0; i < params[0]; i++){
 *          print("hey!");
 *      }
 *  }
 * , 10);
 * }
 * </pre>
 * Would be the same as:
 *
 * <pre>
 * {@code
 *  public void fn(int a){
 *      for(int i = 0; i < a; i++){
 *          print("hey!");
 *      }
 * }
 *
 *fn(10);
 * </pre>
 */
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
