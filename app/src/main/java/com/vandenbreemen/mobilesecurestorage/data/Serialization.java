package com.vandenbreemen.mobilesecurestorage.data;

import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class Serialization {

    private Serialization() {
    }

    /**
     * Convert the given object into serialized bytes
     *
     * @param object
     * @return
     */
    public static byte[] toBytes(Serializable object) {

        //	Write out bytes to a stream based on object output
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            return bos.toByteArray();
        } catch (Exception ex) {
            SystemLog.get().error("Error serializing object!", ex);
            throw new MSSRuntime("Could not serialize data", ex);
        }
    }

    /**
     * Read in an object based on the {@link #toBytes(Object) serialized object}
     *
     * @param bytes
     * @return
     */
    public static Object deserializeBytes(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception ex) {
            SystemLog.get().error("Unable to deserialize data", ex);
            throw new MSSRuntime("Could not de-serialize data", ex);
        }
    }

}
