package com.vandenbreemen.mobilesecurestorage.data;

import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class CloningFactory {

    /**
     * Class representing an array of bytes
     */
    public static final Class<? extends byte[]> BYTE_ARRAY_TYPE = new byte[]{}.getClass();
    private static final Map<Class<? extends Serializable>, ICloner<?>> cloners
            = new HashMap<>();

    /**
     * Register standard cloning operations
     */
    static {
        registerCloner(String.class, object -> new String(object.toCharArray()));

        registerCloner(Integer.class, Integer::valueOf);

        registerCloner(Long.class, Long::valueOf);

        registerCloner(Boolean.class, Boolean.TRUE::equals);

        registerCloner(BYTE_ARRAY_TYPE, (ICloner) object -> {
            byte[] original = (byte[]) object;
            byte[] ret = new byte[original.length];
            for (int i = 0; i < original.length; i++) {
                ret[i] = original[i];
            }
            return ret;
        });
    }

    private CloningFactory() {
    }

    public static <S extends Serializable> ICloner<S> getCloner(Class<S> clz) {
        if (!isClonable(clz))
            throw new MSSRuntime("Object is not cloneable.  Did you call registerCloner() for its class (" + clz.getSimpleName() + ")?");

        return (ICloner<S>) cloners.get(clz);
    }

    /**
     * Register logic for cloning
     *
     * @param clz
     * @param cloner
     */
    public static <S extends Serializable> void registerCloner(Class<S> clz, ICloner<S> cloner) {
        cloners.put(clz, cloner);
    }

    /**
     * Determines whether the given object is clonable
     *
     * @param obj
     * @return
     */
    public static <S extends Serializable> boolean isClonable(S obj) {
        if (obj == null)    //	Null cloning is the trivial case and should always be allowed
            return true;
        return isClonable(obj.getClass());
    }

    /**
     * Returns whether there is a cloner registered for copying the given object
     *
     * @param clz
     * @return
     */
    private static boolean isClonable(Class<? extends Serializable> clz) {
        return cloners.containsKey(clz);
    }

    public static <S extends Serializable> S clone(S object) {
        if (object == null)    //	Null cloning is the trivial case and should always be allowed
            return null;
        if (!isClonable(object.getClass()))
            throw new MSSRuntime("Object is not cloneable.  Did you call registerCloner() for its class (" + object.getClass().getSimpleName() + ")?");

        ICloner<S> cloner = getCloner((Class<S>) object.getClass());
        return cloner.clone(object);
    }

    /**
     * Logic for cloning an object
     *
     * @param <T>
     * @author kevin
     */
    public static interface ICloner<T> {

        /**
         * Clone the given object
         *
         * @param object
         * @return
         */
        public T clone(T object);

    }
}
