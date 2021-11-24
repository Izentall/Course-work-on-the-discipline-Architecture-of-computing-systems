package Charts;

import Common.EventType;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class DeviceChart
{
    private static final double HIGH = 0.5;
    private static final double LOW = 0;
    private int addedEventsAmount;
    private final int maxEventsAmount;
    private final int deviceNumber;
    private double lastValue;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private XYChart.Series series;
    private LineChart<Number,Number> lineChart;

    public DeviceChart(int maxEventsAmount, int deviceNumber)
    {
        this.addedEventsAmount = 0;
        this.maxEventsAmount = maxEventsAmount;
        this.deviceNumber = deviceNumber;
        this.lastValue = LOW;
        this.xAxis = new NumberAxis();
        this.xAxis.setForceZeroInRange(false);
        this.yAxis = new NumberAxis();
        this.yAxis.setUpperBound(2 * HIGH);
        this.series = new XYChart.Series();
        this.series.setName("Device " + deviceNumber);
        series.getData().add(new XYChart.Data<>(0, LOW));
        this.lineChart = new LineChart<>(xAxis, yAxis);
        this.lineChart.setCreateSymbols(false);
        this.lineChart.getData().add(series);
        this.lineChart.setTitle("Device " + deviceNumber);
        this.lineChart.setTitleSide(Side.BOTTOM);
    }

    public LineChart<Number, Number> getChart()
    {
        return lineChart;
    }

    public void addEvent(int deviceNumber, EventType eventType, double time, boolean isContinued)
    {
        if (addedEventsAmount >= maxEventsAmount)
            removeFirstEvent();
        if (deviceNumber != this.deviceNumber)
        {
            series.getData().add(new XYChart.Data<>(time, lastValue));
        }
        else
        {
            switch (eventType)
            {
                case REQUEST ->
                        {
                            series.getData().add(new XYChart.Data<>(time, lastValue));
                            series.getData().add(new XYChart.Data<>(time, HIGH));
                            lastValue = HIGH;
                        }
                case FREE ->
                        {
                            series.getData().add(new XYChart.Data<>(time, lastValue));
                            series.getData().add(new XYChart.Data<>(time, LOW));
                            if (isContinued)
                                series.getData().add(new XYChart.Data<>(time, HIGH));
                            else
                                lastValue = LOW;
                        }
            }
        }
        ++addedEventsAmount;
    }

    private void removeFirstEvent()
    {
        Number xValue = ((XYChart.Data<Number,Number>)series.getData().get(0)).getXValue();
        while (xValue.equals(((XYChart.Data<Number,Number>)series.getData().get(0)).getXValue()))
            series.getData().remove(0);
    }
}
