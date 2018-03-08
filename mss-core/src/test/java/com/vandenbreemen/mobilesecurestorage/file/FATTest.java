package com.vandenbreemen.mobilesecurestorage.file;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.iterableWithSize;

/**
 * <h2>Intro</h2>
 * <p>Validate the behaviour of the FAT
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class FATTest {

    /**
     * System under test
     */
    private FAT sut;

    @Before
    public void setup() {
        this.sut = new FAT();
    }

    @Test
    public void testAddUnit() {
        sut._addUnitFor("test", 1);
        List<Long> units = sut._unitsAllocated("test");

        assertThat(units, allOf(
                iterableWithSize(1),
                hasItem(1L)
        ));
    }

    @Test
    public void testRemoveUnit() {
        sut._addUnitFor("test", 1);
        sut._addUnitFor("test", 2);

        sut._removeUnitFor("test", 2);

        List<Long> units = sut._unitsAllocated("test");
        assertThat(units, allOf(
                hasItem(1L),
                iterableWithSize(1)
        ));
    }

    @Test
    public void testNextAvailableUnitIndex() {
        long nextAvailIndex = sut.nextAvailableUnitIndex();
        assertEquals("First avail unit", 1, nextAvailIndex);
    }

    @Test
    public void testNextAvailUnitAfterWrite() {
        sut._addUnitFor("test", 1);
        assertEquals("Next avail unit", 2, sut.nextAvailableUnitIndex());
    }

    @Test
    public void testNextAvailUnitAfterDelete() {
        sut._addUnitFor("test", 1);
        sut._addUnitFor("test", 2);

        sut._delete("test");

        assertEquals("next avail - after del", 1L, sut.nextAvailableUnitIndex());
    }

    @Test
    public void testListFiles() {
        sut._addUnitFor("File1", 1);
        sut._addUnitFor("File2", 2);

        List<String> fileNames = sut.listFiles();

        assertThat(fileNames, allOf(
                iterableWithSize(2),
                hasItem("File1"), hasItem("File2")
        ));

    }

    @Test
    public void testExists() {
        sut._addUnitFor("File1", 1);
        assertTrue("file exists", sut._exists("File1"));
    }

    @Test
    public void testNotExists() {
        sut._addUnitFor("File1", 1);
        assertFalse("File exists", sut._exists("nonExistent"));
    }

    @Test
    public void testRename() {
        sut._addUnitFor("file1", 1L);
        sut._rename("file1", "newName");

        List<String> fileNames = sut.listFiles();
        assertThat(fileNames, allOf(
                hasItem("newName"), iterableWithSize(1)
        ));
    }

    @Test
    public void testReAllocate() {
        sut._addUnitFor("file1", 1L);
        sut._addUnitFor("file1", 1L);

        assertEquals("Next avail unit", 2l, sut.nextAvailableUnitIndex());
    }

    @Test
    public void testUnAllocate() {
        sut._addUnitFor("file1", 1L);
        sut._addUnitFor("file1", 2L);
        sut._removeUnitFor("file1", 2L);
        sut._addUnitFor("file1", 2L);
        assertEquals("Next avail unit", 3l, sut.nextAvailableUnitIndex());
    }

    @Test
    public void testTouch() {
        sut._touch("test1");
        List<String> files = sut.listFiles();
        assertThat(files, allOf(
                iterableWithSize(1),
                hasItem("test1")
        ));
    }

}
