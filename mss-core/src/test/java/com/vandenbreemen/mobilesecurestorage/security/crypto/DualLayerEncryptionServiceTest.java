package com.vandenbreemen.mobilesecurestorage.security.crypto;

import com.vandenbreemen.mobilesecurestorage.security.BytesToBits;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;

import org.junit.Test;

/**
 * @author kevin
 */
public class DualLayerEncryptionServiceTest {

    @Test
    public void testDisplayBytesInDerivedKey() {

        SecureString key = SecureString.fromPassword("test");
        KeySet derived = DualLayerEncryptionService.generateKeys(key);

        System.out.println("key1:  " + BytesToBits.toByteString(derived.getKey(KeySet.KEYNUM.Key1).copyBytes()));
        System.out.println("key2:  " + BytesToBits.toByteString(derived.getKey(KeySet.KEYNUM.Key2).copyBytes()));

    }

}