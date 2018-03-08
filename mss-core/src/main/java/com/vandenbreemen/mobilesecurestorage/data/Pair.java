package com.vandenbreemen.mobilesecurestorage.data;

import com.vandenbreemen.mobilesecurestorage.security.SecureString;

import java.io.Serializable;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class Pair<T, U> {

    private T first;
    private U second;

    public Pair(T first, U second) {

        //	Copy first and second values if they are cloneable so that finalize() will not corrupt referential data!
        if (first instanceof Serializable && CloningFactory.isClonable((Serializable) first)) {
            first = (T) CloningFactory.clone((Serializable) first);
        }
        if (second instanceof Serializable && CloningFactory.isClonable((Serializable) second)) {
            second = (U) CloningFactory.clone((Serializable) second);
        }

        this.first = first;
        this.second = second;
    }


    public T first() {
        return first;
    }


    public U second() {
        return second;
    }

    /**
     * Sets the value of the first.  Note that during finalization of this pair this object may
     * be destroyed!
     *
     * @param first
     */
    public void setFirst(T first) {
        this.first = first;
    }


    /**
     * Sets the value of the second.  Note that during finalization of this pair this object may
     * be destroyed!
     *
     * @param second
     */
    public void setSecond(U second) {
        this.second = second;
    }


    public String toString() {
        return first + ", " + second;
    }

    /**
     * Makes an effort to securely wipe the contents of this pair from
     * memory.  If that effort fails (No instructions are available for the
     * particular type of values contained in this pair) then sets the contents
     * to null.
     */
    @Override
    protected void finalize() { //  NOSONAR I'd like to be able to wipe these by hand if I need to
        wipe(first);
        wipe(second);
    }

    private void wipe(Object v) {
        if (v instanceof SecureString)
            ((SecureString) v).finalize();  //  NOSONAR Need to be able to do this explicitly
    }
}
