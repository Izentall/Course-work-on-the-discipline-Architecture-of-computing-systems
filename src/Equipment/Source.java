package Equipment;

import Common.Request;

import java.util.LinkedList;

public class Source
{
    private final int sourceNumber;
    private final int requestAmount;
    private final double flow;

    public Source(int sourceNumber, int requestAmount, double flow)
    {
        if (requestAmount <= 0)
            throw new IllegalArgumentException("Request amount must be positive");
        if (flow <= 0)
            throw new IllegalArgumentException("Flow must be positive");
        this.sourceNumber = sourceNumber;
        this.flow = flow;
        this.requestAmount = requestAmount;
    }

    public int getSourceNumber()
    {
        return sourceNumber;
    }

    public int getRequestAmount()
    {
        return requestAmount;
    }

    public LinkedList<Request> getRequestQueue()
    {
        double time = 0;
        LinkedList<Request> queue = new LinkedList<>();
        for (int i = 1; i <= requestAmount; ++i)
        {
            time += -Math.log(Math.random()) / flow;
            queue.addLast(new Request(sourceNumber, i, time));
        }
        return queue;
    }
}
