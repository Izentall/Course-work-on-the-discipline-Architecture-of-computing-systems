package Common;

public class Request
{
    private final int sourceNumber;
    private final int serialNumber;
    private final double generationTime;
    private double waitingTime;
    private double processTime;

    public Request(int sourceNumber, int serialNumber, double generationTime)
    {
        if (generationTime < 0)
            throw new IllegalArgumentException("Generation time must be non-negative");
        this.sourceNumber = sourceNumber;
        this.serialNumber = serialNumber;
        this.generationTime = generationTime;
        this.waitingTime = 0;
        this.processTime = 0;
    }

    public int getSourceNumber()
    {
        return sourceNumber;
    }

    public int getSerialNumber()
    {
        return serialNumber;
    }

    public double getGenerationTime()
    {
        return generationTime;
    }

    public double getWaitingTime()
    {
        return waitingTime;
    }

    public double getProcessTime()
    {
        return processTime;
    }

    public void setWaitingTime(double waitingTime)
    {
        this.waitingTime = waitingTime;
    }

    public void setProcessTime(double processTime)
    {
        this.processTime = processTime;
    }
}
