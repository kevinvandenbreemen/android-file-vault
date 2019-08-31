package com.vandenbreemen.mobilesecurestorage.security.crypto;

import com.vandenbreemen.mobilesecurestorage.data.Serialization;
import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.security.BytesToBits;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;

import org.spongycastle.crypto.engines.AESEngine;
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
public class AESEncryptionService implements EncryptionService, ObjectEncryptor {

    private static byte[] getCorrectKeyLength(byte[] key) {
        return new BytesToBits().padTo(key, 256);
    }

    private byte[] doEncryption(byte[] key, byte[] clear) {

        List<SecureString> keyBuckets = new ArrayList<>();

        //  Hash the key
        key = BytesToBits.secureHash(key);
        keyBuckets.add(new SecureString(key));

        key = getCorrectKeyLength(key);
        keyBuckets.add(new SecureString(key));

        try {
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));

            byte[] ivBytes = new byte[16];

            //  Randomize the IV
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
            SystemLog.get().error("Error encrypting", e);
        } finally {
            for (SecureString ss : keyBuckets) {
                ss.finalize();
            }
        }
        return new byte[0];
    }

    private byte[] doDecryption(byte[] key, byte[] encrypted) {
        List<SecureString> keyBuckets = new ArrayList<>();

        //  Hash the password
        key = BytesToBits.secureHash(key);
        keyBuckets.add(new SecureString(key));

        key = getCorrectKeyLength(key);
        keyBuckets.add(new SecureString(key));

        try {
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
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
            SystemLog.get().error("Error decryption", e);
        } finally {
            for (SecureString ss : keyBuckets) {
                ss.finalize();
            }
        }
        return new byte[0];
    }

    @Override
    public String getName() {
        return "AES";
    }


    @Override
    public final byte[] decrypt(byte[] ciphertext, SecureString password) {
        return doDecryption(password.getBytes(), ciphertext);
    }

    @Override
    public final byte[] encrypt(byte[] plaintext, SecureString password) {
        return doEncryption(password.getBytes(), plaintext);
    }

    @Override
    public final byte[] encryptObject(SecureString password, Serializable object) {
        byte[] serialized = Serialization.toBytes(object);
        return encrypt(serialized, password);
    }

    @Override
    public final Object decryptObject(byte[] ciphertext, SecureString password) {
        byte[] plaintext = decrypt(ciphertext, password);
        return Serialization.deserializeBytes(plaintext);
    }
}
