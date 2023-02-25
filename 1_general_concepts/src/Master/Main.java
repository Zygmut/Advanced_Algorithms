package Master;

import java.time.Duration;
import java.util.stream.Collectors;

import Request.Request;
import Request.RequestCode;
import mesurament.Mesurament;

public class Main {

    public static void main(String[] args) throws Exception {
        //Mesurament.mesura();
        MVC mvc = new MVC();

        mvc.getView().initConfig("config.txt");
        mvc.getView().start();

        mvc.notifyRequest(new Request(RequestCode.All_methods, "Main"));
        mvc.notifyRequest(new Request(RequestCode.All_methods, "Main"));
        mvc.notifyRequest(new Request(RequestCode.All_methods, "Main"));
        mvc.notifyRequest(new Request(RequestCode.All_methods, "Main"));

        System.out.println("Times in Nanosecods");
        System.out.println("\tEscalar N^2: " + mvc.getModel().getEscalarTimes().stream().mapToLong(Duration::toNanos).mapToObj(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("\tmode NLogN : " + mvc.getModel().getModeNlognTimes().stream().mapToLong(Duration::toNanos).mapToObj(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("\tmode N     : " + mvc.getModel().getModeNTimes().stream().mapToLong(Duration::toNanos).mapToObj(String::valueOf).collect(Collectors.joining(", ")));
    }
}
