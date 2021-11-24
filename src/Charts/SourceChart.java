package Charts;

import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class SourceChart
{
    private static final double HIGH = 0.5;
    private static final double LOW = 0;
    private int addedEventsAmount;
    private final int maxEventsAmount;
    private final int sourceNumber;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private XYChart.Series series;
    private LineChart<Number,Number> lineChart;

    public SourceChart(int maxEventsAmount, int sourceNumber)
    {
        this.addedEventsAmount = 0;
        this.maxEventsAmount = maxEventsAmount;
        this.sourceNumber = sourceNumber;
        this.xAxis = new NumberAxis();
        this.xAxis.setForceZeroInRange(false);
        this.yAxis = new NumberAxis();
        this.yAxis.setUpperBound(2 * HIGH);
        this.series = new XYChart.Series();
        this.series.setName("Source " + sourceNumber);
        series.getData().add(new XYChart.Data<>(0, LOW));
        this.lineChart = new LineChart<>(xAxis, yAxis);
        this.lineChart.setCreateSymbols(false);
        this.lineChart.getData().add(series);
        this.lineChart.setTitle("Source " + sourceNumber);
        this.lineChart.setTitleSide(Side.BOTTOM);
    }

    public LineChart<Number, Number> getChart()
    {
        return lineChart;
    }

    public void addEvent(int sourceNumber, double time)
    {
        if (addedEventsAmount >= maxEventsAmount)
            removeFirstEvent();
        if (sourceNumber != this.sourceNumber)
        {
            series.getData().add(new XYChart.Data<>(time, LOW));
        }
        else
        {
            series.getData().add(new XYChart.Data<>(time, LOW));
            series.getData().add(new XYChart.Data<>(time, HIGH));
            series.getData().add(new XYChart.Data<>(time, LOW));
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
