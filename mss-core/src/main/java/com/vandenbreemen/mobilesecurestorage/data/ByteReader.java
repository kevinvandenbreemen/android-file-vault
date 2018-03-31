package com.vandenbreemen.mobilesecurestorage.data;

import static com.vandenbreemen.mobilesecurestorage.data.ControlBytes.END_OF_HEADER;

/**
 * <h2>Intro</h2>
 * <p>Facilitates reading streams of bytes
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class ByteReader {


    public byte[] readBytes(byte[] data) {

        int indexOf = -1;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == END_OF_HEADER) {
                indexOf = i;
                break;
            }
        }

        if (indexOf < 0) {
            return data;
        }

        byte[] ret = new byte[indexOf];
        System.arraycopy(data, 0, ret, 0, ret.length);

        return ret;
    }
}
