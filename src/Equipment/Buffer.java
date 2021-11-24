package Equipment;

import Common.Request;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Buffer
{
    private final int bufferSize;
    private int currentMinimumSourceNumber;
    private  LinkedList<Request> buffer;

    public Buffer(int bufferSize)
    {
        this.bufferSize = bufferSize;
        this.currentMinimumSourceNumber = Integer.MAX_VALUE;
        this.buffer = new LinkedList<Request>();
    }

    public boolean isEmpty()
    {
        return buffer.isEmpty();
    }

    public boolean addIfPossible(Request request)
    {
        if (buffer.size() == bufferSize)
            return false;
        buffer.addLast(request);
        if (request.getSourceNumber() < currentMinimumSourceNumber)
            currentMinimumSourceNumber = request.getSourceNumber();
        return true;
    }

    public boolean contains(int sourceNumber)
    {
        for (Request request: buffer)
        {
            if (request.getSourceNumber() == sourceNumber)
                return true;
        }
        return false;
    }

    public Request getRequest()
    {
        for (Request request: buffer)
        {
            if (request.getSourceNumber() == currentMinimumSourceNumber)
            {
                buffer.remove(request);
                setMinimumSourceNumber();
                return request;
            }
        }
        throw new NoSuchElementException();
    }

    public Request getRequest(int sourceNumber)
    {
        for (Request request: buffer)
        {
            if (request.getSourceNumber() == sourceNumber)
            {
                buffer.remove(request);
                setMinimumSourceNumber();
                return request;
            }
        }
        throw new NoSuchElementException();
    }

    private void setMinimumSourceNumber()
    {
        int min = Integer.MAX_VALUE;
        for (Request request : buffer)
        {
            if (request.getSourceNumber() < min)
                min = request.getSourceNumber();
        }
        currentMinimumSourceNumber = min;
    }
}
