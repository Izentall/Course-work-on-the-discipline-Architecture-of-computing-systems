package Equipment;

import Common.Request;

public class Device
{
    private final int deviceNumber;
    private double flow;
    private double usingTime;
    private int lastSourceNumber;
    private boolean isFree;

    public Device(int deviceNumber, double flow)
    {
        if (flow <= 0)
            throw new IllegalArgumentException("Flow must be positive");
        this.deviceNumber = deviceNumber;
        this.flow = flow;
        this.usingTime = 0;
        this.lastSourceNumber = 0;
        this.isFree = true;
    }

    public int getDeviceNumber()
    {
        return deviceNumber;
    }

    public double getUsingTime()
    {
        return usingTime;
    }

    public double getFlow()
    {
        return flow;
    }

    public int getLastSourceNumber()
    {
        return lastSourceNumber;
    }

    public void setFlow(double flow)
    {
        this.flow = flow;
    }

    public void refresh()
    {
        usingTime = 0;
    }

    public double processingRequest(Request request)
    {
        double serviceTime = (Math.random() + 0.5) / flow;
        request.setProcessTime(serviceTime);
        lastSourceNumber = request.getSourceNumber();
        usingTime += serviceTime;
        return serviceTime + request.getGenerationTime() + request.getWaitingTime();
    }

    public boolean isFree()
    {
        return isFree;
    }

    public void take()
    {
        isFree = false;
    }

    public void free()
    {
        isFree = true;
    }
}
