package net.miblue.chron;

/**
 * Created by Main on 10/25/15.
 */
public class Job {
    private long start;
    private long repeat;
    private String key;
    private Execution execution;

    public Job(Execution e, long s, long r, String k){
        start = s;
        repeat = r * 1000;
        execution = e;
        key = k;
    }

    public String getKey() {
        return key;
    }

    public long getRepeat() {

        return repeat;
    }

    public long getStart() {

        return start;
    }

    public Execution getExecution() {

        return execution;
    }
}
