package Common;

import Equipment.Device;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

public class Statistic
{
    private int[] m;
    private int[] n;
    private double[] p;
    private double[] processTime;
    private double[] waitingTime;
    private double[] processTimeVariance;
    private double[] waitingTimeVariance;
    private double[] usage;
    private ArrayList<ArrayList<Double>> eachProcessTime;
    private ArrayList<ArrayList<Double>> eachWaitingTime;
    private double totalTime;

    public Statistic(int numberOfSources, int numberOfDevices)
    {
        m = new int[numberOfSources];
        n = new int[numberOfSources];
        p = new double[numberOfSources];
        processTime = new double[numberOfSources];
        waitingTime = new double[numberOfSources];
        processTimeVariance = new double[numberOfSources];
        waitingTimeVariance = new double[numberOfSources];
        usage = new double[numberOfDevices];
        eachProcessTime = new ArrayList<>(numberOfSources);
        eachWaitingTime = new ArrayList<>(numberOfSources);
        for (int i = 0; i < numberOfSources; i++)
        {
            eachProcessTime.add(new ArrayList<>());
            eachWaitingTime.add(new ArrayList<>());
        }
        totalTime = 0;
    }

    public void clear()
    {
        for (int i = 0; i < m.length; i++)
        {
            m[i] = 0;
            n[i] = 0;
            p[i] = 0;
            processTime[i] = 0;
            waitingTime[i] = 0;
            processTimeVariance[i] = 0;
            waitingTimeVariance[i] = 0;
            eachProcessTime.set(i, new ArrayList<>());
            eachWaitingTime.set(i, new ArrayList<>());
        }
        Arrays.fill(usage, 0);
        totalTime = 0;
    }

    public void addRequest(int sourceNumber)
    {
        n[sourceNumber - 1] += 1;
    }

    public void addRequest(Request request)
    {
        int requestNumber = request.getSourceNumber() - 1;
        m[requestNumber] += 1;
        waitingTime[requestNumber] += request.getWaitingTime();
        processTime[requestNumber] += request.getProcessTime();
        eachProcessTime.get(requestNumber).add(request.getProcessTime());
        eachWaitingTime.get(requestNumber).add(request.getWaitingTime());
    }

    public void addUsage(ArrayList<Device> devices, double totalTime)
    {
        this.totalTime = totalTime;
        for (Device device: devices)
        {
            usage[device.getDeviceNumber() - 1] = device.getUsingTime() / totalTime;
        }
    }

    public void calculate()
    {
        for (int i = 0; i < m.length; ++i)
        {
            p[i] = 1 - (double)m[i] / n[i];
            processTime[i] /= m[i];
            waitingTime[i] /= m[i];
            for (Double elem: eachProcessTime.get(i))
            {
                processTimeVariance[i] += Math.pow(elem - processTime[i], 2);
            }
            processTimeVariance[i] /= m[i];
            for (Double elem: eachWaitingTime.get(i))
            {
                waitingTimeVariance[i] += Math.pow(elem - waitingTime[i], 2);
            }
            waitingTimeVariance[i] /= m[i];
        }
    }

    public double[] getP()
    {
        return p;
    }

    public boolean checkP(double[] p0)
    {
        for (int i = 0; i < p.length; i++)
        {
            if (Math.abs(p[i] - p0[i]) < 0.1 * p0[i])
                return true;
        }
        return false;
    }

    public void print()
    {
        Object[][] table = new String[p.length + 1][];
        table[0] = new String[] { "Source number", "Requests amount", "P of rejection", "Pass time", "Waiting time",
                "Processing time", "Waiting variation", "Processing variation" };
        for (int i = 0; i < p.length; i++)
        {
            String[] row = new String[8];
            row[0] = Integer.toString(i + 1);
            row[1] = Integer.toString(n[i]);
            row[2] = format(p[i]);
            row[3] = format(processTime[i] + waitingTime[i]);
            row[4] = format(waitingTime[i]);
            row[5] = format(processTime[i]);
            row[6] = format(waitingTimeVariance[i]);
            row[7] = format(processTimeVariance[i]);
            table[i + 1] = row;
        }

        for (final Object[] row : table) {
            System.out.format("%15s%20s%20s%20s%20s%20s%25s%25s\n", row);
        }

        table = new String[usage.length + 1][];
        table[0] = new String[] { "Device number", "Usage coefficient"};
        for (int i = 0; i < usage.length; i++)
        {
            table[i + 1] = new String[] { Integer.toString(i + 1), format(usage[i]) };
        }

        for (final Object[] row : table) {
            System.out.format("%20s%25s\n", row);
        }
    }

    private static String format(double num) {
        DecimalFormatSymbols decimalSymbols = DecimalFormatSymbols.getInstance();
        decimalSymbols.setDecimalSeparator(',');
        return new DecimalFormat("0.000", decimalSymbols).format(num);
    }
}
