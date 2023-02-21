package Main;

import java.time.Duration;
import java.util.Arrays;

import Request.Request;
import Request.RequestCode;
import Utils.TimeProfiler;
import View.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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

    public void master(MVC mvc) {
        Arrays.stream(TimeProfiler.batchTimeIt(this::fn, 10))
                .mapToLong(Duration::toMillis)
                .forEach(System.out::println);

        System.out.println(new Request(RequestCode.None, this, this));

    }

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        MVC mvc = new MVC();
        Application.launch(mvc.getView().getClass(), args);
        (new Main()).master(mvc);
    }
}
