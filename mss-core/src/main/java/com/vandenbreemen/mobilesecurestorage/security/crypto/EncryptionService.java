package com.vandenbreemen.mobilesecurestorage.security.crypto;

import com.vandenbreemen.mobilesecurestorage.security.SecureString;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public interface EncryptionService {

    /**
     * Decrypt with a secure string password
     *
     * @param ciphertext
     * @param password
     * @return
     */
    public byte[] decrypt(byte[] ciphertext, SecureString password);

    /**
     * Encrypt using a secure string
     *
     * @param plaintext
     * @param password
     * @return
     */
    public byte[] encrypt(byte[] plaintext, SecureString password);

    /**
     * Get name of this encryption service
     *
     * @return
     */
    public String getName();

}
