package edu.usc.irds.autoext.apted;

import edu.usc.irds.lang.Function;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mapper for converting strings to integer.
 * Uses counters for mapping.
 * Optionally writes contents to file
 */
public class StringToIntMapper implements Function<String, Integer>, AutoCloseable {

    private Map<String, Integer> map = new HashMap<>();
    private Map<Integer, String> reverseMap = new HashMap<>();
    private boolean persist = false;
    private AtomicInteger counter = new AtomicInteger(0);
    private BufferedWriter writer;

    /**
     * creates a mapper instance which uses counters.
     * For persistent based mapper see {@link #StringToIntMapper(File)}
     */
    public StringToIntMapper(){
    }

    /**
     * This instance writes the mapping to given file.
     * Should be closed at the end to flush the contents to file
     * @param file file instance
     * @throws FileNotFoundException
     */
    public StringToIntMapper(File file) throws FileNotFoundException {
        this();
        this.persist = true;
        this.writer = new BufferedWriter(new PrintWriter(file));
    }

    @Override
    public Integer apply(String obj) {
        return this.map(obj);
    }

    /**
     * Maps a string to integer
     * @param obj the object which requires mapping
     * @return integer obtained after mapping
     */
    public Integer map(String obj){
        Integer mapped = map.get(obj);
        if (mapped == null) {
            mapped = counter.incrementAndGet();
            map.put(obj, mapped);
            reverseMap.put(mapped, obj);
            if (persist){
                try {
                    writer.write(obj);
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mapped;
    }

    /**
     * returns key that was mapped to this value
     * @param val the value for reverse lookup
     * @return String if present, null if not present
     */
    public String reverseMap(Integer val){
        return this.reverseMap.get(val);
    }

    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
        }
    }

}
