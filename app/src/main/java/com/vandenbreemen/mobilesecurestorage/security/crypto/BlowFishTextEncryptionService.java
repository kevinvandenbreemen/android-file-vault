package com.vandenbreemen.mobilesecurestorage.security.crypto;

import com.vandenbreemen.mobilesecurestorage.data.Serialization;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;
import com.vandenbreemen.mobilesecurestorage.security.BytesToBits;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;

import org.spongycastle.crypto.engines.TwofishEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class BlowFishTextEncryptionService implements EncryptionService, ObjectEncryptor {
    private static byte[] getCorrectKeyLength(byte[] key) {
        return new BytesToBits().padTo(key, 256);
    }

    private byte[] encryptSpongy(byte[] key, byte[] clear) {

        List<SecureString> keyStrings = new ArrayList<SecureString>();

        key = getCorrectKeyLength(key);
        keyStrings.add(new SecureString(key));

        try {
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new TwofishEngine()));
            // Random iv

            byte[] ivBytes = new byte[16];
            SecureRandom rng = new SecureRandom();
            rng.nextBytes(ivBytes);

            cipher.init(true, new ParametersWithIV(new KeyParameter(key), ivBytes));
            byte[] outBuf = new byte[cipher.getOutputSize(clear.length)];

            int processed = cipher.processBytes(clear, 0, clear.length, outBuf, 0);
            processed += cipher.doFinal(outBuf, processed);

            byte[] outBuf2 = new byte[processed + 16];        // Make room for iv
            System.arraycopy(ivBytes, 0, outBuf2, 0, 16);    // Add iv
            System.arraycopy(outBuf, 0, outBuf2, 16, processed);    // Then the encrypted data

            return outBuf2;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (SecureString ss : keyStrings) {    //	Dump temp keys from memory
                ss.finalize();
            }
        }
        return new byte[0];
    }

    private byte[] decryptSpongy(byte[] key, byte[] encrypted) {

        List<SecureString> keyStrings = new ArrayList<SecureString>();

        key = getCorrectKeyLength(key);
        keyStrings.add(new SecureString(key));

        try {
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new TwofishEngine()));
            byte[] ivBytes = new byte[16];
            System.arraycopy(encrypted, 0, ivBytes, 0, ivBytes.length); // Get iv from data
            byte[] dataonly = new byte[encrypted.length - ivBytes.length];
            System.arraycopy(encrypted, ivBytes.length, dataonly, 0, encrypted.length - ivBytes.length);

            cipher.init(false, new ParametersWithIV(new KeyParameter(key), ivBytes));
            byte[] clear = new byte[cipher.getOutputSize(dataonly.length)];
            int len = cipher.processBytes(dataonly, 0, dataonly.length, clear, 0);
            len += cipher.doFinal(clear, len);

            return clear;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (SecureString ss : keyStrings) {    //	Dump temp keys from memory
                ss.finalize();
            }
        }
        return new byte[0];
    }

    /**
     * True by default, allows you to turn off randomized IV.  This is dangerous as it makes decryption
     * easier.
     *
     * @param randomize
     * @return
     */
    public BlowFishTextEncryptionService randomizeIV(boolean randomize) {
        throw new MSSRuntime("Blowfish cipher does not support non-random IVs");
    }

    @Override
    public String getName() {
        return "Twofish";
    }

    @Override
    public final byte[] decrypt(byte[] ciphertext, SecureString password) {
        return decryptSpongy(password.getBytes(), ciphertext);
    }

    @Override
    public final byte[] encrypt(byte[] plaintext, SecureString password) {
        return encryptSpongy(password.getBytes(), plaintext);
    }


    /* (non-Javadoc)
     * @see com.vandenbreemen.android.common.security.crypto.IObjectEncryptor#encryptObject(com.vandenbreemen.android.common.security.crypto.SecureString, java.io.Serializable)
     */
    @Override
    public final byte[] encryptObject(SecureString password, Serializable object) {
        byte[] serialized = Serialization.toBytes(object);
        return encrypt(serialized, password);
    }

    /* (non-Javadoc)
     * @see com.vandenbreemen.android.common.security.crypto.IObjectEncryptor#decryptObject(byte[], com.vandenbreemen.android.common.security.crypto.SecureString)
     */
    @Override
    public final Object decryptObject(byte[] ciphertext, SecureString password) {
        byte[] plaintext = decrypt(ciphertext, password);
        return Serialization.deserializeBytes(plaintext);
    }
}
