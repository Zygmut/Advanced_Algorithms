package Master;

import java.time.Duration;
import java.util.stream.Collectors;

public class Main {

    private static MVC mvc;

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        mvc = new MVC(".\\1_general_concepts\\config.txt");

        // mvc.notifyRequest(new Request(RequestCode.All_methods, "Main"));

        System.out.println("Times in Nanosecods");
        System.out.println("\tEscalar N^2: " + mvc.getModel().getEscalarTimes().stream().mapToLong(Duration::toNanos)
                .mapToObj(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("\tmode NLogN : " + mvc.getModel().getModeNlognTimes().stream().mapToLong(Duration::toNanos)
                .mapToObj(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("\tmode N     : " + mvc.getModel().getModeNTimes().stream().mapToLong(Duration::toNanos)
                .mapToObj(String::valueOf).collect(Collectors.joining(", ")));
    }

}
