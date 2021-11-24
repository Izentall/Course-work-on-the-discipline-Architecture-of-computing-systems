import Charts.BufferChart;
import Charts.DeviceChart;
import Charts.SourceChart;
import Common.*;
import Equipment.Buffer;
import Equipment.Device;
import Equipment.Source;
import javafx.event.ActionEvent;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Arrays;

public class RequestProcessor implements Runnable
{
    private ArrayList<Source> sources;
    private ArrayList<Device> devices;
    private Buffer buffer;
    private Calendar calendar;
    private Statistic statistic;
    private Event lastEvent;
    private final int numberOfSources;
    private final double sourceFlow;
    private final int numberOfDevices;
    private final double deviceFlow;
    private final int bufferSize;
    private int[] requestsPerSource;
    private static final int INITIAL_NUMBER_OF_REQUESTS = 10000;
    private static final double STUDENT_COEFFICIENT = 1.643;
    private static final double RELATIVE_ACCURACY = 0.1;
    private BufferChart bufferChart;
    private ArrayList<DeviceChart> deviceCharts;
    private ArrayList<SourceChart> sourceCharts;
    private Button btnStep;
    private Button btnResume;
    private boolean isStepByStepEnded;
    private boolean areGraphsNeeded;

    public RequestProcessor(int numberOfSources, double sourceFlow, int numberOfDevices, double deviceFlow, int bufferSize)
    {
        this.numberOfSources = numberOfSources;
        this.sourceFlow = sourceFlow;
        this.numberOfDevices = numberOfDevices;
        this.deviceFlow = deviceFlow;
        this.bufferSize = bufferSize;
        this.requestsPerSource = new int[numberOfSources];
        Arrays.fill(this.requestsPerSource, INITIAL_NUMBER_OF_REQUESTS);
        this.bufferChart = new BufferChart(bufferSize, 10);
        this.deviceCharts = new ArrayList<>(numberOfDevices);
        for (int i = 0; i < numberOfDevices; i++)
        {
            deviceCharts.add(new DeviceChart(10, i + 1));
        }
        this.sourceCharts = new ArrayList<>(numberOfSources);
        for (int i = 0; i < numberOfSources; i++)
        {
            sourceCharts.add(new SourceChart(10, i + 1));
        }
        btnStep = new Button();
        btnStep.setText("Next event");
        btnStep.setOnAction((ActionEvent event) -> {
            if (!calendar.isEmpty())
            {
                processEvent();
                calendar.deleteEvent(lastEvent);
            }
            else
            {
                continueProcessing();
                run();
            }
        });
        btnResume = new Button();
        btnResume.setText("Resume");
        btnResume.setOnAction((ActionEvent event) -> {
            areGraphsNeeded = false;
            continueProcessing();
            run();
        });
        isStepByStepEnded = false;
        areGraphsNeeded = true;
        init();
    }

    public void process()
    {
        areGraphsNeeded = false;
        continueProcessing();
        run();
    }

    private void init()
    {
        this.sources = new ArrayList<>();
        for (int i = 0; i < numberOfSources; ++i)
        {
            this.sources.add(new Source(i + 1, requestsPerSource[i], sourceFlow));
        }
        this.devices = new ArrayList<>();
        for (int i = 0; i < numberOfDevices; ++i)
        {
            this.devices.add(new Device(i + 1, deviceFlow));
        }
        this.buffer = new Buffer(bufferSize);
        this.calendar = new Calendar(sources);
        this.statistic = new Statistic(numberOfSources, numberOfDevices);
        this.lastEvent = null;
    }

    private void processIteration()
    {
        init();
        continueProcessing();
    }

    private void setRequestsPerSource(double[] pOld)
    {
        double maxN = 0;
        for (int i = 0; i < pOld.length; i++)
        {
            int n = (int) Math.round((Math.pow(STUDENT_COEFFICIENT, 2) * (1 - pOld[i]))
                    / (pOld[i] * Math.pow(RELATIVE_ACCURACY, 2)));
            if (n > maxN)
            {
                maxN = n;
            }
            requestsPerSource[i] = (int) Math.round((Math.pow(STUDENT_COEFFICIENT, 2) * (1 - pOld[i]))
                    / (pOld[i] * Math.pow(RELATIVE_ACCURACY, 2)));
        }
        Arrays.fill(requestsPerSource, (int) maxN);
    }

    private void processEvent()
    {
        Event event = calendar.getEvent();
        switch (event.getType())
        {
            case REQUEST ->
                    {
                        processRequest(event.getRequest());
                    }
            case FREE ->
                    {
                        processDevice(event.getDeviceNumber(), event.getTime());
                    }
        }
        lastEvent = event;
    }

    private void continueProcessing()
    {
        while (!calendar.isEmpty())
        {
            processEvent();
            calendar.deleteEvent(lastEvent);
        }
        statistic.addUsage(devices, lastEvent.getTime());
        statistic.calculate();
        isStepByStepEnded = true;
    }

    private void processRequest(Request request)
    {
        statistic.addRequest(request.getSourceNumber());
        Device device = null;
        for (Device d: devices)
        {
            if (d.isFree())
            {
                device = d;
                break;
            }
        }
        if (device != null)
        {
            device.take();
            double processingTime = device.processingRequest(request);
            calendar.addEvent(EventType.FREE, processingTime, device.getDeviceNumber(), null);
            statistic.addRequest(request);
            if (!isStepByStepEnded && areGraphsNeeded)
            {
                for (int i = 0; i < numberOfDevices; i++)
                {
                    deviceCharts.get(i).addEvent(device.getDeviceNumber(), EventType.REQUEST, request.getGenerationTime(), false);
                }
                bufferChart.addEvent(EventType.REQUEST, request.getGenerationTime(), false);
            }
        }
        else
        {
            if (buffer.addIfPossible(request) && !isStepByStepEnded && areGraphsNeeded)
                bufferChart.addEvent(EventType.REQUEST, request.getGenerationTime(), true);
            if (!isStepByStepEnded && areGraphsNeeded)
            {
                for (int i = 0; i < numberOfDevices; i++)
                {
                    deviceCharts.get(i).addEvent(-1, EventType.REQUEST, request.getGenerationTime(), false);
                }
            }
        }
        if (!isStepByStepEnded && areGraphsNeeded)
        {
            for (int i = 0; i < numberOfSources; i++)
            {
                sourceCharts.get(i).addEvent(request.getSourceNumber(), request.getGenerationTime());
            }
        }
    }

    private void processDevice(int deviceNumber, double time)
    {
        Device device = devices.get(deviceNumber - 1);
        if (!buffer.isEmpty())
        {
            if (buffer.contains(device.getLastSourceNumber()))
            {
                Request request = buffer.getRequest(device.getLastSourceNumber());
                request.setWaitingTime(time - request.getGenerationTime());
                double processingTime = device.processingRequest(request);
                statistic.addRequest(request);
                calendar.addEvent(EventType.FREE, processingTime, deviceNumber, null);
            }
            else
            {
                Request request = buffer.getRequest();
                request.setWaitingTime(time - request.getGenerationTime());
                double processingTime = device.processingRequest(request);
                statistic.addRequest(request);
                calendar.addEvent(EventType.FREE, processingTime, deviceNumber, null);
            }
            if (!isStepByStepEnded && areGraphsNeeded)
            {
                for (int i = 0; i < numberOfDevices; i++)
                {
                    deviceCharts.get(i).addEvent(deviceNumber, EventType.FREE, time, true);
                }
            }
        }
        else
        {
            device.free();
            if (!isStepByStepEnded && areGraphsNeeded)
            {
                for (int i = 0; i < numberOfDevices; i++)
                {
                    deviceCharts.get(i).addEvent(deviceNumber, EventType.FREE, time, false);
                }
            }
        }
        if (!isStepByStepEnded && areGraphsNeeded)
        {
            bufferChart.addEvent(EventType.FREE, time, true);
            for (int i = 0; i < numberOfSources; i++)
            {
                sourceCharts.get(i).addEvent(-1, time);
            }
        }
    }

    public LineChart<Number, Number> getBufferChart()
    {
        return bufferChart.getChart();
    }

    public ArrayList<DeviceChart> getDeviceCharts()
    {
        return deviceCharts;
    }

    public ArrayList<SourceChart> getSourceCharts()
    {
        return sourceCharts;
    }

    public Button getBtnStep()
    {
        return btnStep;
    }

    public Button getBtnResume()
    {
        return btnResume;
    }

    @Override
    public void run()
    {
        while (!isStepByStepEnded)
        {
            ;
        }
        Statistic statisticOld;
        double[] pOld;
        do
        {
            pOld = Arrays.copyOf(statistic.getP(), statistic.getP().length);
            statisticOld = statistic;
            setRequestsPerSource(pOld);
            processIteration();
        } while (!statistic.checkP(pOld));
        statisticOld.print();
    }
}
