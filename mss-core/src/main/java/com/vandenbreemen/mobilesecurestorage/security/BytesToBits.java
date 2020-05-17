package com.vandenbreemen.mobilesecurestorage.security;

import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;

import org.spongycastle.crypto.generators.SCrypt;
import org.spongycastle.jcajce.provider.digest.SHA256;
import org.spongycastle.jcajce.provider.digest.SHA3;
import org.spongycastle.util.Arrays;

import java.util.BitSet;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class BytesToBits {
    /**
     * Default length in bits for an initialization vector.  This allows for at least one block in a block cipher
     * always to be generated regardless of the size of the plaintext
     */
    public static final int IV_DEFAULT_LEN = 128;

    /**
     * 16 bytes per block!
     */
    public static final int IV_DEFAULT_LEN_BYTE = IV_DEFAULT_LEN / 8;

    /**
     * Salt length in bytes.  Twice as long as regular hash output
     */
    public static final int SALT_LENGTH = 64;

    /**
     * Bit generation for padding
     */
    private BitGenerator bitGenerator = BitGenerator.DEFAULT_0;

    public BytesToBits() {
    }

    /**
     * Convert the given byte array into a {@link BitSet}
     *
     * @param bytes
     * @return
     */
    //	See also http://www.coderanch.com/t/412032/java/java/bit-byte
    public static int[] toBits(byte[] bytes) {
        int[] ret = new int[bytes.length * 8];
        int bIndex = 0;
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {    //	Assumes there are only 8 bits in a byte...  Should be true
                ret[bIndex] = ((b & 1) == 1/* Convert bit  */) ? 1 : 0;
                b >>= 1;
                bIndex++;                                //	Set the index of bit we're setting
            }
        }

        return ret;
    }

    /**
     * Remove zeroed off blocks
     *
     * @param bytes
     * @param blockSize Block size supported by the cipher you want to use
     * @return
     */
    public static byte[] stripZeros(byte[] bytes, int blockSize) {
        int finalByteSize = bytes.length;
        for (int j = bytes.length - 1; j > 0; j--) {
            if (bytes[j] != 0) {
                break;
            } else
                finalByteSize--;

            //	If we've shaved off enough zeros to get us to something that's
            //	works out to be blocks of the desired block size then we're done
            if ((finalByteSize * 8) % blockSize == 0)
                break;

        }
        byte[] ret = new byte[finalByteSize];
        System.arraycopy(bytes, 0, ret, 0, finalByteSize);
        return ret;
    }

    /**
     * Generates a string representation of all the bits in the given byte array
     *
     * @param bytes
     * @return
     */
    public static String toBitString(byte[] bytes) {
        int[] bits = toBits(bytes);
        StringBuilder bld = new StringBuilder();
        for (int b : bits) {
            bld.append(b).append(", ");
        }
        String ret = bld.toString();
        return ret.substring(0, ret.length() - ", ".length());
    }

    /**
     * For debugging - Generates a string representing the bytes
     * @param bytes
     * @return
     */
    public static String toByteString(byte[] bytes) {
        StringBuilder bld = new StringBuilder();
        for (byte b : bytes) {

            //  Convert to unsigned byte so it's easier to compare with C output
            int uint = b & 0xFF;

            bld.append("[" + uint + "] ");
        }

        return bld.toString();
    }

    /**
     * Use secure hashing algorithms to produce a hash
     *
     * @param data
     * @return
     */
    public static byte[] secureHash(byte[] data) {

        //	First hash using SHA2
        SHA256.Digest firstDigest = new SHA256.Digest();
        firstDigest.update(data);
        data = firstDigest.digest();

        //	Finally hash using SHA-3
        SHA3.DigestSHA3 digest = new SHA3.DigestSHA3(256);
        digest.update(data);
        return digest.digest();
    }

    /**
     * Scrypt-based hashing to stretch a password.
     *
     * @param data
     * @return
     */
    //	See https://github.com/timw/bc-java/commit/7e84a85c46dfff2606ba9d6e603882d3605da051
    public static byte[] superStrongHash(byte[] data, byte[] chosenSalt) {
        return SCrypt.generate(data, chosenSalt, 64, 1, 164, 32);
    }

    /**
     * Scrypt-based hashing to stretch a password.  Scrypt is parametrized to severely stress memory and parallelization resources.
     * This algorithm will typically take about 5 seconds at most
     * to complete so that the resultant product will take same CPU time per attempt!
     *
     * @param data
     * @return
     */
    //	See https://github.com/timw/bc-java/commit/7e84a85c46dfff2606ba9d6e603882d3605da051
    public static byte[] obscenelyDifficultHash(byte[] data, byte[] chosenSalt) {
        return SCrypt.generate(data, chosenSalt,
                //	CPU/memory cost
                100,
                //	Block size for hashing
                1,
                //	Parallellization
                300,
                //	Size of generated hash (32 byte -> 256 bit)
                32);
    }

    /**
     * Get the length in bits of the given byte array
     *
     * @param bytes
     * @return
     */
    public static int getLength(byte[] bytes) {
        return bytes != null ?
                bytes.length * 8 : 0;
    }

    /**
     * Gets appropriate bytes for use in encryption
     *
     * @param key
     * @return
     */
    public static byte[] getBytes(String key) {
        try {
            return key.getBytes("UTF-8");
        } catch (Exception ex) {
            SystemLog.get().error("Error getting bytes", ex);
            return null;
        }
    }

    /**
     * Given the array of bytes pads it so that the specified length of bits is satisfied.  For example pad 24 bytes to 32 bytes so that 256 bits satisfied
     *
     * @param bytes
     * @param bitLength
     * @return
     */
    public byte[] padTo(byte[] bytes, int bitLength) {
        if (bitLength % 8 != 0)
            throw new MSSRuntime("Cannot pad to a number of bits that is not divisible by 8.  You specified " + bitLength);

        int diff = bitLength - getLength(bytes);
        if (diff < 0) {
            return bytes;
        }

        byte[] diffBytes = new byte[diff / 8];    //	Padding bytes
        byte tempByte;
        int nextBit;
        for (int i = 0; i < diffBytes.length; i++) {
            tempByte = 0;

            for (int j = 0; j < 8; j++) {
                nextBit = bitGenerator.getBit(j * (i + 1));
                if (nextBit == 1)
                    tempByte = (byte) (tempByte | (1 << j));
                else
                    tempByte = (byte) (tempByte & ~(1 << j));
            }
            diffBytes[i] = tempByte;
        }

        return Arrays.concatenate(bytes, diffBytes);

    }

    /**
     * @param bytes
     * @param desiredLength
     * @return
     */
    public byte[] zeroPad(byte[] bytes, int desiredLength) {
        int diffNumBits = desiredLength - bytes.length;
        if (diffNumBits <= 0) {
            return bytes;
        }

        byte[] padded = new byte[desiredLength];
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        return padded;
    }
}
