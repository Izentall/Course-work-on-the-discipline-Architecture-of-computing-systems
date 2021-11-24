package Common;


public class Event implements Comparable<Event>
{
    private final int serialNumber;
    private final EventType eventType;
    private final double time;
    private int deviceNumber;
    private Request request;

    public Event(int serialNumber, EventType eventType, double time, int deviceNumber, Request request)
    {
        if (time < 0)
            throw new IllegalArgumentException("Time must be non-negative");
        this.serialNumber = serialNumber;
        this.eventType = eventType;
        this.time = time;
        switch (eventType)
        {
            case REQUEST ->
                    {
                        this.deviceNumber = 0;
                        this.request = request;
                    }
            case FREE ->
                    {
                        this.deviceNumber = deviceNumber;
                        this.request = null;
                    }
            case END ->
                    {
                        this.deviceNumber = 0;
                        this.request = null;
                    }
        }
    }

    public int getSerialNumber()
    {
        return serialNumber;
    }

    public EventType getType()
    {
        return eventType;
    }

    public double getTime()
    {
        return time;
    }

    public int getDeviceNumber()
    {
        return deviceNumber;
    }

    public Request getRequest()
    {
        return request;
    }

    @Override
    public int compareTo(Event r)
    {
        return Double.compare(time, r.time);
    }
}
