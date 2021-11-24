import Charts.DeviceChart;
import Charts.SourceChart;
import Common.Statistic;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;


public class Main extends Application {

    static int numberOfSources = 10;
    static double sourceFlow = 1;
    static int numberOfDevices = 10;
    static double deviceFlow = 1;
    static int bufferSize = 10;

    @Override
    public void start(Stage primaryStage)
    {
        RequestProcessor requestProcessor = new RequestProcessor(numberOfSources,sourceFlow, numberOfDevices,deviceFlow,bufferSize);

        Button btnStep = requestProcessor.getBtnStep();
        Button btnResume = requestProcessor.getBtnResume();
        LineChart<Number, Number> buffer = requestProcessor.getBufferChart();
        ArrayList<DeviceChart> devices = requestProcessor.getDeviceCharts();
        ArrayList<SourceChart> sources = requestProcessor.getSourceCharts();

        HBox buttons = new HBox();
        buttons.getChildren().addAll(btnStep, btnResume);

        TilePane sourcesBox = new TilePane();
        sourcesBox.setVgap(50);
        sourcesBox.setHgap(50);
        for (SourceChart sourceChart:sources)
        {
            sourceChart.getChart().setMaxSize(100, 100);
            sourcesBox.getChildren().add(sourceChart.getChart());
        }

        TilePane devicesBox = new TilePane();
        devicesBox.setVgap(50);
        devicesBox.setHgap(50);
        for (DeviceChart deviceChart: devices)
        {
            deviceChart.getChart().setMaxSize(100, 100);
            sourcesBox.getChildren().add(deviceChart.getChart());
        }

        HBox charts = new HBox();
        charts.getChildren().add(sourcesBox);
        charts.getChildren().add(buffer);
        charts.getChildren().add(devicesBox);

        TilePane tilePane = new TilePane();
        tilePane.setVgap(50);
        tilePane.setHgap(50);
        tilePane.setOrientation(Orientation.VERTICAL);
        tilePane.getChildren().addAll(charts);

        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(buttons, tilePane);

        AnchorPane root = new AnchorPane();
        root.getChildren().add(mainBox);

        Scene scene = new Scene(root, 1800, 600);

        primaryStage.setTitle("Graphics");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

    }

    public static void main(String[] args) throws InterruptedException
    {
        launch(args);
        /*for (int i = 0; i < 6; i++)
        {
            System.out.println(deviceFlow + i * 0.02);
            RequestProcessor requestProcessor = new RequestProcessor(numberOfSources, sourceFlow, numberOfDevices, deviceFlow + i * 0.02, bufferSize);
            requestProcessor.process();
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }*/
    }

}