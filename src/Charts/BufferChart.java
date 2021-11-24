package Charts;

import Common.EventType;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Arrays;

public class BufferChart
{
    private static final double HIGH = 0.5;
    private static final double LOW = 0;
    private int addedEventsAmount;
    private final int maxEventsAmount;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private LineChart<Number,Number> lineChart;
    private ArrayList<XYChart.Series> seriesArrayList;
    private double[] lastValue;

    public BufferChart(int bufferSize, int maxEventsAmount)
    {
        this.addedEventsAmount = 0;
        this.maxEventsAmount = maxEventsAmount;
        this.xAxis = new NumberAxis();
        this.xAxis.setForceZeroInRange(false);
        this.yAxis = new NumberAxis();
        this.lineChart = new LineChart<>(xAxis,yAxis);
        this.lineChart.setCreateSymbols(false);
        this.seriesArrayList = new ArrayList<>(bufferSize);
        for (int i = 0; i < bufferSize; ++i)
        {
            seriesArrayList.add(new XYChart.Series());
            seriesArrayList.get(i).setName("Buffer " + (i + 1));
            seriesArrayList.get(i).getData().add(new XYChart.Data(0, LOW + i));
            lineChart.getData().add(seriesArrayList.get(i));
        }
        this.lastValue = new double[bufferSize];
        Arrays.fill(lastValue, LOW);
    }

    public LineChart<Number, Number> getChart()
    {
        return lineChart;
    }

    public void addEvent(EventType eventType, double time, boolean isChanged)
    {
        int maxHighIndex = getMaxHighIndex();
        for (int i = 0; i < seriesArrayList.size(); ++i)
        {
            if (addedEventsAmount >= maxEventsAmount)
                removeFirstEvent(seriesArrayList.get(i));
            if (isChanged)
            {
                addLastEvent(eventType, time, i, maxHighIndex);
            }
            else
            {
                seriesArrayList.get(i).getData().add(new XYChart.Data(time, lastValue[i] + i));
            }
            ++addedEventsAmount;
        }
    }

    private void removeFirstEvent(XYChart.Series<Number,Number> series)
    {
        Number xValue = series.getData().get(0).getXValue();
        while (xValue.equals(series.getData().get(0).getXValue()))
            series.getData().remove(0);
    }

    private void addLastEvent(EventType eventType, double time, int index, int maxHighIndex)
    {
        switch (eventType)
        {
            case REQUEST ->
                    {
                        if (index != maxHighIndex + 1)
                        {
                            seriesArrayList.get(index).getData().add(new XYChart.Data(time, lastValue[index] + index));
                        }
                        else
                        {
                            seriesArrayList.get(index).getData().add(new XYChart.Data(time, LOW + index));
                            seriesArrayList.get(index).getData().add(new XYChart.Data(time, HIGH + index));
                            lastValue[index] = HIGH;
                        }
                    }
            case FREE ->
                    {
                        if (index != maxHighIndex)
                        {
                            seriesArrayList.get(index).getData().add(new XYChart.Data(time, lastValue[index] + index));
                        }
                        else
                        {
                            seriesArrayList.get(index).getData().add(new XYChart.Data(time, HIGH + index));
                            seriesArrayList.get(index).getData().add(new XYChart.Data(time, LOW + index));
                            lastValue[index] = LOW;
                        }
                    }
        }
    }

    private int getMaxHighIndex()
    {
        int index = -1;
        for (int i = 0; i < lastValue.length; ++i)
        {
            if (lastValue[i] == LOW)
                return index;
            ++index;
        }
        return index;
    }
}
