package com.vandenbreemen.mobilesecurestorage.security.crypto;

import com.vandenbreemen.mobilesecurestorage.data.Serialization;
import com.vandenbreemen.mobilesecurestorage.security.BytesToBits;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;

import java.io.Serializable;

/**
 * <h2>Intro</h2>
 * <p>Two layers of encryption.  Passwords are hashed multiple times for each layer
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class DualLayerEncryptionService implements EncryptionService, ObjectEncryptor {

    private EncryptionService firstLayer;
    private EncryptionService secondLayer;

    public DualLayerEncryptionService() {
        this.firstLayer = new BlowFishTextEncryptionService();        //	Blowfish
        this.secondLayer = new AESEncryptionService();    //	Then AES
    }

    /**
     * Generate a suitably strong set of two keys out of the given password.  The original password will be finalized after execution
     * of this method!
     *
     * @param password
     * @return
     */
    public static KeySet generateKeys(SecureString password) {

        //	First generate a pseudorandom string using thousands of rounds of secure hash of the password
        SecureString keySpace = new SecureString(password.copyBytes());
        for (int i = 0; i < 5000; i++) {
            keySpace.setBytes(BytesToBits.secureHash(keySpace.copyBytes()));
        }

        //	Update keyspace with ultrasecure scrypt hash before continuing
        keySpace.setBytes(BytesToBits.superStrongHash(keySpace.copyBytes(), BytesToBits.secureHash(password.copyBytes())));

        KeySet ret = new KeySet();
        ret.setKey(KeySet.KEYNUM.Key1, new SecureString(BytesToBits.secureHash(keySpace.copyBytes()) /*Hash this in case parameters of SCRYPT are included in hash*/));

        //	Now generate an scrypt-based hash
        ret.setKey(KeySet.KEYNUM.Key2, new SecureString(
                BytesToBits.secureHash(  /*Hash this in case parameters of SCRYPT are included in hash*/
                        BytesToBits.obscenelyDifficultHash(keySpace.copyBytes(), BytesToBits.secureHash(password.getBytes())))));

        keySpace.finalize();
        password.finalize();

        return ret;

    }

    @Override
    public final byte[] decrypt(byte[] ciphertext, SecureString password) {

        if (!(password instanceof KeySet))
            throw new IllegalArgumentException("Password expected to be a " + KeySet.class.getSimpleName());

        KeySet keySet = (KeySet) password;

        byte[] pass = secondLayer.decrypt(ciphertext, keySet.getKey(KeySet.KEYNUM.Key2));
        pass = BytesToBits.stripZeros(pass, 128);
        pass = firstLayer.decrypt(pass, keySet.getKey(KeySet.KEYNUM.Key1));

        return pass;
    }

    @Override
    public final byte[] encrypt(byte[] plaintext, SecureString password) {

        if (!(password instanceof KeySet))
            throw new IllegalArgumentException("Password expected to be a " + KeySet.class.getSimpleName());

        KeySet keySet = (KeySet) password;

        byte[] encrypted = firstLayer.encrypt(plaintext, keySet.getKey(KeySet.KEYNUM.Key1));                //	First layer of encryption
        encrypted = secondLayer.encrypt(encrypted, keySet.getKey(KeySet.KEYNUM.Key2));

        return encrypted;
    }

    @Override
    public String getName() {
        return "Balance between security and performance";
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
