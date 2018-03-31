package com.vandenbreemen.mobilesecurestorage.data;

/**
 * <h2>Intro</h2>
 * <p>Standard control bytes for binary files.  See also http://www.asciitable.com/
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class ControlBytes {

    /**
     * Start of a medium.  Generally a medium refers to a cell that contains both metadata and content.  The content
     * should be indicated using the {@link #START_OF_CONTENT} byte, while anything before that byte should consist
     * of control bytes and values (metadata etc).
     */
    public static final byte START_OF_MEDIUM = 1;
    /**
     * End of a medium
     */
    public static final byte END_OF_MEDIUM = 25;
    /**
     * New page in a medium
     */
    public static final byte NEW_PAGE = 12;
    /**
     * Start of actual content.  Note that no end of content byte should be required.  Instead systems should utilize
     * the {@link #LENGTH_IND} byte to determine where the end is.  This allows for encoding raw data without the need
     * to escape it in case a byte that is the same as a chosen END OF CONTENT byte is encountered.
     */
    public static final byte START_OF_CONTENT = 2;
    /**
     * Length of data
     */
    public static final byte LENGTH_IND = 17;

    /**
     * This byte signifies the premature end of a header in the space allocated
     * for that header.
     */
    public static final byte END_OF_HEADER = 4;

    private ControlBytes() {
    }

}
