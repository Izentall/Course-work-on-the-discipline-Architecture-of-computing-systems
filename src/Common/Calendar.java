package Common;

import Equipment.Source;

import java.util.LinkedList;
import java.util.List;

public class Calendar
{
    private LinkedList<Event> calendar;
    private int currentSerialNumber;

    public Calendar(List<Source> sources)
    {
        currentSerialNumber = 1;
        calendar = new LinkedList<>();
        for (Source source: sources)
        {
            LinkedList<Request> queue = source.getRequestQueue();
            for (Request request: queue)
            {
                calendar.add(new Event(currentSerialNumber, EventType.REQUEST, request.getGenerationTime(), request.getSourceNumber(), request));
                ++currentSerialNumber;
            }
        }
        calendar.sort(Event::compareTo);
    }

    public Event getEvent()
    {
        return calendar.getFirst();
    }

    public boolean isEmpty()
    {
        return calendar.isEmpty();
    }

    public void addEvent(EventType eventType, double time, int deviceNumber, Request request)
    {
        for (Event event : calendar)
        {
            if (time < event.getTime())
            {
                calendar.add(calendar.indexOf(event), new Event(currentSerialNumber, eventType, time, deviceNumber, request));
                ++currentSerialNumber;
                break;
            }
        }
    }

    public void deleteEvent(Event event)
    {
        calendar.remove(event);
    }
}
