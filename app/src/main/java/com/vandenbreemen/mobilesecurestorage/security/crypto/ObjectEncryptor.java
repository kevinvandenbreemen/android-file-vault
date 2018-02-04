package com.vandenbreemen.mobilesecurestorage.security.crypto;

import com.vandenbreemen.mobilesecurestorage.security.SecureString;

import java.io.Serializable;

/**
 * <h2>Intro</h2>
 * <p>Service that can encrypt/decrypt entire serializables in Java
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public interface ObjectEncryptor {
    /**
     * Encrypt object using password
     *
     * @param password
     * @param object
     * @return Bytes representing encrypted serialized form of the object
     */
    byte[] encryptObject(SecureString password,
                         Serializable object);

    /**
     * Decrypt the bytes and deserialize them into an object
     *
     * @param ciphertext
     * @param password
     * @return
     */
    Object decryptObject(byte[] ciphertext, SecureString password);
}
