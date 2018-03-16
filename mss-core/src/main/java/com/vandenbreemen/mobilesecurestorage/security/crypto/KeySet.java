package com.vandenbreemen.mobilesecurestorage.security.crypto;

import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class KeySet extends SecureString {
    /**
     *
     */
    private static final long serialVersionUID = 3527894210537909494L;
    /**
     * The actual keys
     */
    Map<KEYNUM, SecureString> keyset;

    public KeySet() {
        super();
        this.keyset = new HashMap<KEYNUM, SecureString>();
    }

    /**
     * Sets the key for the given {@link KEYNUM}.
     *
     * @param keyNum
     * @param key
     */
    public final void setKey(KEYNUM keyNum, SecureString key) {
        this.keyset.put(keyNum, key);
    }

    /**
     * This implementation throws a non-supported operation error and directs the coder to the {@link #getKey(KEYNUM)} method instead
     */
    @Override
    public final byte[] getBytes() {
        throw new UnsupportedOperationException("Directly accessing bytes is not possible on a " + KeySet.class.getSimpleName() + ".  Please use getKey() instead");
    }

    /**
     * Wipe out the keys in memory
     */
    @Override
    public final void finalize() {

        if (isFinalized())    //	Just do nothing.
            return;

        for (SecureString v : keyset.values()) {
            v.randomFinalize();
        }

        super.finalize();

    }

    /**
     * Get the key at the key number
     *
     * @param keyNum
     * @return
     */
    public final SecureString getKey(KEYNUM keyNum) {

        if (isFinalized())
            throw new MSSRuntime("This KeySet appears to have been finalized.  Not allowing key access to continue to protect data");

        if (keyset.get(keyNum) == null)
            throw new MSSRuntime("Key at " + keyNum + " not populated.  Probably a coding problem");
        return keyset.get(keyNum);
    }

    @Override
    public final SecureString copy() {
        KeySet ret = new KeySet();
        keyset.entrySet().forEach(entry -> {
            ret.setKey(entry.getKey(), entry.getValue().copy());
        });
        return ret;
    }

    @Override
    public final boolean equals(SecureString anotherSet) {
        if (!(anotherSet instanceof KeySet))
            return false;
        if (this.isFinalized() || anotherSet.isFinalized())
            return false;

        KeySet otherKeySet = (KeySet) anotherSet;
        if (this.keyset.size() != otherKeySet.keyset.size())
            return false;

        for (KEYNUM keyNum : this.keyset.keySet()) {
            if (!otherKeySet.keyset.containsKey(keyNum))
                return false;
            if (!otherKeySet.keyset.get(keyNum).equals(this.keyset.get(keyNum)))
                return false;
        }

        return true;
    }

    /**
     * Enumerated keys
     *
     * @author kevin
     */
    public static enum KEYNUM {
        Key1,
        Key2,
        Key3,
        Key4,
    }
}
