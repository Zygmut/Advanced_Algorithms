package Master;

import java.time.Duration;
import java.util.stream.Collectors;

import javax.swing.JButton;

import Request.Request;
import Request.RequestCode;
import View.Section;
import View.Section.DirectionAndPosition;
import mesurament.Mesurament;
import java.awt.Color;

public class Main {

    private static MVC mvc;

    public static void main(String[] args) throws Exception {
        Mesurament.mesura();
        mvc = new MVC();

        mvc.getView().initConfig("config.txt");
        mvc.getView().addSection(createButtonSection(), DirectionAndPosition.POSITION_TOP);
        mvc.getView().addSection(createChartSection(), DirectionAndPosition.POSITION_CENTER);
        mvc.getView().addSection(createLegendSection(), DirectionAndPosition.POSITION_RIGHT);
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

    private static Section createLegendSection() {
        Section legendSection = new Section();
        String columnLabels[] = { "Escalar", "Mode NLogN", "Mode N"};
        Color colors[] = {
                Color.RED,
                Color.YELLOW,
                Color.BLUE,
        };
        legendSection.addLegend(columnLabels, colors, DirectionAndPosition.DIRECTION_COLUMN, -1);
        return legendSection;
    }

    private static Section createChartSection() {
        Section chartSection = new Section();
        String columnLabels[] = { "A", "B", "C"};
        String shh[] = { "A", "B"};
        Color colors[] = {
                Color.RED,
                Color.YELLOW,
                Color.BLUE,
        };
        int[][] data = {{20, 40, 60, 80, 150}, {10, 30, 50, 70, 90}, {5, 25, 45, 65, 85}};
        chartSection.createLineChart(shh, data, colors, columnLabels);
        return chartSection;
    }

    private static Section createButtonSection() {
        JButton[] buttons = new JButton[4];
        buttons[0] = new JButton("Escalar");
        buttons[0].addActionListener(e -> {
            System.out.println("Escalar");
        });
        buttons[1] = new JButton("Mode NLogN");
        buttons[1].addActionListener(e -> {
            System.out.println("Mode NLogN");
        });
        buttons[2] = new JButton("Mode N");
        buttons[2].addActionListener(e -> {
            System.out.println("Mode N");
        });
        buttons[3] = new JButton("All");
        buttons[3].addActionListener(e -> {
            System.out.println("All");
        });
        Section buttonsSection = new Section();
        buttonsSection.addButtons(buttons, DirectionAndPosition.DIRECTION_ROW);
        return buttonsSection;
    }
}
