package com.vandenbreemen.mobilesecurestorage.security;

import org.spongycastle.pqc.math.linearalgebra.ByteUtils;
import org.spongycastle.util.encoders.Base64;

import java.io.Serializable;

/**
 * <h2>Intro</h2>
 * <p>Charsequence that is designed to be wiped on finalization (preferably explicitly in the code).  Useful for storing
 * passwords etc.
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class SecureString implements CharSequence, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 54387882724060417L;
    /**
     * Bytes inside the string
     */
    private volatile byte[] bytes;

    private volatile boolean isFinalized;

    /**
     * Initializes this secure string using the given array of bytes.  Please note
     * that the given bytes' contents will be zeroed out once this {@link SecureString} has been
     * dropped from use
     *
     * @param bytes
     */
    public SecureString(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Create a secure string using a single byte
     */
    public SecureString(byte b) {
        this(new byte[]{b});
    }

    public SecureString() {
        this.bytes = new byte[0];
    }

    /**
     * Generate a new {@link SecureString} based on a known password string.  The contents will
     * be the bytes resulting from getting the string's bytes in UTF-8.
     *
     * @param password
     * @return
     */
    public static SecureString fromPassword(String password) {
        return new SecureString(Base64.encode(password.getBytes()));
    }

    /**
     * Returns true if the given secure string is the same as this one
     *
     * @param anotherString
     * @return
     */
    public boolean equals(SecureString anotherString) {
        if (isFinalized() || anotherString.isFinalized())    //	Finalized strings cannot ever be equal
            return false;
        return ByteUtils.equals(this.bytes, anotherString.bytes);
    }

    @Override
    public final char charAt(int location) {
        return (char) bytes[location];
    }

    /**
     * Append the byte array to the end of the list
     *
     * @param toAppend Bytes to be appended
     */
    public final void addBytes(byte[] toAppend) {
        byte[] newBytes = new byte[bytes.length + toAppend.length];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        System.arraycopy(toAppend, 0, newBytes, bytes.length, toAppend.length);
        Bytes.wipe(bytes);
        bytes = newBytes;
    }

    @Override
    public final int length() {
        return bytes.length;
    }

    private final byte[] getSubsequence(int start, int end) {
        byte[] ret = new byte[end - start];
        System.arraycopy(bytes, start, ret, 0, ret.length);
        return ret;
    }

    @Override
    public final CharSequence subSequence(int start, int end) {
        return new SecureString(getSubsequence(start, end));
    }

    /**
     * This implementation zeros out the contents
     */
    @Override
    public void finalize() {
        Bytes.wipe(bytes);
        isFinalized = true;
    }

    /**
     * Equivilent of {@link #finalize()} except that the overwrite is done
     * using random bytes.  Do this for {@link SecureString}s that contain things
     * like encryption keys.
     */
    public void randomFinalize() {
        Bytes.wipeRandom(bytes);
        isFinalized = true;
    }

    /**
     * Returns the exact byte array.  This is intentional so that during finalization
     * the array can be zeroed
     *
     * @return
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Completely obliterate the current content of this secure string and replace it
     * with the given bytes
     *
     * @param bytes
     */
    public final void setBytes(byte[] bytes) {
        Bytes.wipe(this.bytes);    //	Secure wipe the current content
        this.bytes = bytes;                //	And replace it with the incoming
    }

    /**
     * Use this only in cases where you want to be guaranteed garbage collection won't happen between
     * {@link #getBytes()} and using the value of {@link #getBytes()}.
     *
     * @return
     */
    public byte[] copyBytes() {
        byte[] ret = new byte[bytes.length];
        System.arraycopy(bytes, 0, ret, 0, bytes.length);
        return ret;
    }

    public SecureString copy() {
        return new SecureString(copyBytes());
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (byte b : bytes) {
            ret.append((char) b);
        }
        return ret.toString();
    }

    /**
     * Taske a {@link BytesToBits#secureHash(byte[]) secure hash-based} hash and returns the hash code of the string representation of that.
     */
    @Override
    public int hashCode() {
        return new String(BytesToBits.secureHash(getBytes())).hashCode();
    }

    /**
     * Comparison based on the hash of the two secure strings' contents.
     */
    @Override
    public boolean equals(Object anotherString) {
        if (!(anotherString instanceof SecureString))
            return false;
        return hashCode() == ((SecureString) anotherString).hashCode();
    }

    /**
     * Returns true if this {@link SecureString} is no longer valid for use (has been {@link #finalize() finalized})
     *
     * @return
     */
    public final boolean isFinalized() {
        return isFinalized;
    }
}
