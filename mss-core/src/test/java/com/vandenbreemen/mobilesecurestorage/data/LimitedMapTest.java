package com.vandenbreemen.mobilesecurestorage.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class LimitedMapTest {

    @Test
    public void testMaxSize() {
        LimitedMap<String, String> limitedMap = new LimitedMap<>(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");
        limitedMap.put("3", "Moe");

        assertEquals("Max size should be enforced", 2, limitedMap.size());

    }

    @Test
    public void testInsertionOrder() {
        LimitedMap<String, String> limitedMap = new LimitedMap<>(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");
        limitedMap.put("3", "Moe");

        List<String> keys = new ArrayList<>(limitedMap.keySet());
        assertEquals("First key should be 2", "2", keys.get(0));
        assertEquals("Last key should be 3", "3", keys.get(1));

    }

    @Test
    public void testRemoveTriggersEviction() {

        AtomicBoolean evictCalled = new AtomicBoolean(false);

        LimitedMap<String, String> limitedMap = new LimitedMap<String, String>(2) {
            @Override
            protected void onEvict(Object key, String value) {
                evictCalled.set(true);
            }
        };
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");
        limitedMap.put("3", "Moe");

        assertTrue("Eviction expected", evictCalled.get());
    }

    @Test
    public void testClear() {
        AtomicBoolean evictCalled = new AtomicBoolean(false);

        LimitedMap<String, String> limitedMap = new LimitedMap<String, String>(2) {
            @Override
            protected void onEvict(Object key, String value) {
                evictCalled.set(true);
            }
        };
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");
        limitedMap.clear();

        assertTrue("Eviction expected", evictCalled.get());
        assertEquals("Empty", 0, limitedMap.size());
    }

    @Test
    public void testRemovalThenEvictionHasExpectedSize() {
        LimitedMap<String, String> limitedMap = new LimitedMap<>(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");

        limitedMap.remove("2");
        limitedMap.put("3", "Moe");

        Set<String> keys = limitedMap.keySet();
        assertEquals("Two keys only expected", 2, keys.size());
    }

    @Test
    public void testRemovalThenEviction() {
        LimitedMap<String, String> limitedMap = new LimitedMap<>(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");

        limitedMap.remove("2");
        limitedMap.put("3", "Moe");

        Set<String> keys = limitedMap.keySet();
        assertTrue("First key expected", keys.contains("1"));
        assertTrue("Third key expected", keys.contains("3"));


    }

    @Test
    public void testRemovalThenOversizeForceEviction() {
        LimitedMap<String, String> limitedMap = new LimitedMap<>(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");

        limitedMap.remove("2");
        limitedMap.put("3", "Moe");
        limitedMap.put("4", "Moe");

        Set<String> keys = limitedMap.keySet();
        assertTrue("Third key expected", keys.contains("3"));
        assertTrue("Forth key expected", keys.contains("4"));


    }

    @Test
    public void testRemovalThenOversizeForceEvictionSize() {
        LimitedMap<String, String> limitedMap = new LimitedMap<>(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");

        limitedMap.remove("2");
        limitedMap.put("3", "Moe");
        limitedMap.put("4", "Moe");

        Set<String> keys = limitedMap.keySet();
        assertEquals("Only 2 entries expected", 2, keys.size());


    }


    @Test
    public void concTtestMaxSize() {
        LimitedMap<String, String> limitedMap = LimitedMap.concurrentMap(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");
        limitedMap.put("3", "Moe");

        assertEquals("Max size should be enforced", 2, limitedMap.size());

    }

    @Test
    public void concTtestInsertionOrder() {
        LimitedMap<String, String> limitedMap = LimitedMap.concurrentMap(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");
        limitedMap.put("3", "Moe");

        List<String> keys = new ArrayList<>(limitedMap.keySet());
        assertEquals("First key should be 2", "2", keys.get(0));
        assertEquals("Last key should be 3", "3", keys.get(1));

    }

    @Test
    public void concTtestRemoveTriggersEviction() {

        AtomicBoolean evictCalled = new AtomicBoolean(false);

        LimitedMap<String, String> limitedMap = LimitedMap.concurrentMap(2, (k, v) -> {
            evictCalled.set(true);
            return null;
        });
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");
        limitedMap.put("3", "Moe");

        assertTrue("Eviction expected", evictCalled.get());
    }

    @Test
    public void concTtestRemovalThenEvictionHasExpectedSize() {
        LimitedMap<String, String> limitedMap = LimitedMap.concurrentMap(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");

        limitedMap.remove("2");
        limitedMap.put("3", "Moe");

        Set<String> keys = limitedMap.keySet();
        assertEquals("Two keys only expected", 2, keys.size());
    }

    @Test
    public void concTtestRemovalThenEviction() {
        LimitedMap<String, String> limitedMap = LimitedMap.concurrentMap(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");

        limitedMap.remove("2");
        limitedMap.put("3", "Moe");

        Set<String> keys = limitedMap.keySet();
        assertTrue("First key expected", keys.contains("1"));
        assertTrue("Third key expected", keys.contains("3"));


    }

    @Test
    public void concTtestRemovalThenOversizeForceEviction() {
        LimitedMap<String, String> limitedMap = LimitedMap.concurrentMap(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");

        limitedMap.remove("2");
        limitedMap.put("3", "Moe");
        limitedMap.put("4", "Moe");

        Set<String> keys = limitedMap.keySet();
        assertTrue("Third key expected", keys.contains("3"));
        assertTrue("Forth key expected", keys.contains("4"));


    }

    @Test
    public void concTtestRemovalThenOversizeForceEvictionSize() {
        LimitedMap<String, String> limitedMap = LimitedMap.concurrentMap(2);
        limitedMap.put("1", "Larry");
        limitedMap.put("2", "Curly");

        limitedMap.remove("2");
        limitedMap.put("3", "Moe");
        limitedMap.put("4", "Moe");

        Set<String> keys = limitedMap.keySet();
        assertEquals("Only 2 entries expected", 2, keys.size());


    }
}
