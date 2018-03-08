package com.vandenbreemen.mobilesecurestorage.file;

import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class DefaultFileSystemTestListener implements FileSystemTestListener {

    private List<Long> readTimes;

    private List<Long> writeTimes;

    /**
     * Start time of current read
     */
    private Long startTimeOfCurrentRead;

    /**
     * Start time of current write
     */
    private Long startTimeOfCurrentWrite;

    public DefaultFileSystemTestListener() {
        //	Insertion needs to be fast.  Reading can take longer
        this.readTimes = new LinkedList<Long>();
        this.writeTimes = new LinkedList<Long>();
    }

    @Override
    public void logReadStart() {
        if (startTimeOfCurrentRead != null)
            throw new MSSRuntime("Probable logic error:  Last read never stopped");
        startTimeOfCurrentRead = System.currentTimeMillis();
    }

    @Override
    public void logReadEnd() {
        if (startTimeOfCurrentRead == null)
            throw new MSSRuntime("Probably logic error:  Read logging never started");
        readTimes.add(System.currentTimeMillis() - startTimeOfCurrentRead);
        startTimeOfCurrentRead = null;
    }

    @Override
    public void logWriteStart() {
        if (startTimeOfCurrentWrite != null)
            throw new MSSRuntime("Probable logic error:  Last write never stopped");
        startTimeOfCurrentWrite = System.currentTimeMillis();
    }

    @Override
    public void logWriteEnd() {
        if (startTimeOfCurrentWrite == null)
            throw new MSSRuntime("Probably logic error:  Write logging never started");
        writeTimes.add(System.currentTimeMillis() - startTimeOfCurrentWrite);
        startTimeOfCurrentWrite = null;
    }

    /**
     * Get average value of the given set of values
     *
     * @param values
     * @return
     */
    private long getAverage(List<Long> values) {
        long sum = 0;
        for (Long v : values) {
            sum += v;
        }

        return Double.valueOf(Math.ceil((double) sum / (double) values.size())).longValue();
    }

    @Override
    public long getAverageReadTime() {
        if (CollectionUtils.isEmpty(readTimes)) throw new MSSRuntime("No read samples ever taken");
        return getAverage(readTimes);
    }

    @Override
    public long getAverageWriteTime() {
        if (CollectionUtils.isEmpty(writeTimes))
            throw new MSSRuntime("No write samples ever taken");
        return getAverage(writeTimes);
    }

    /**
     * Gets a printable report of average read/write times
     *
     * @return
     */
    public String getAverageTimesReport() {
        StringBuilder bld = new StringBuilder("PERFORMANCE REPORT:\n");
        bld.append("READ TIME(ms):\t").append(getAverageReadTime()).append("\tSAMPLES:\t").append(readTimes.size()).append("\n");
        bld.append("WRITE TIME(ms):\t").append(getAverageWriteTime()).append("\tSAMPLES:\t").append(writeTimes.size()).append("\n");
        return bld.toString();
    }
}