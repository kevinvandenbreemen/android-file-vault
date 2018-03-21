package com.vandenbreemen.mobilesecurestorage.security.crypto.persistence;

import com.vandenbreemen.mobilesecurestorage.data.Pair;
import com.vandenbreemen.mobilesecurestorage.file.ChainedUnit;
import com.vandenbreemen.mobilesecurestorage.file.ChunkedMediumException;
import com.vandenbreemen.mobilesecurestorage.file.IndexedFile;
import com.vandenbreemen.mobilesecurestorage.log.SystemLog;
import com.vandenbreemen.mobilesecurestorage.message.MSSRuntime;
import com.vandenbreemen.mobilesecurestorage.patterns.ProgressListener;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.DualLayerEncryptionService;
import com.vandenbreemen.mobilesecurestorage.security.crypto.KeySet;
import com.vandenbreemen.mobilesecurestorage.security.crypto.ObjectEncryptor;

import java.io.File;
import java.util.function.Consumer;

/**
 * <h2>Intro</h2>
 * <p>Re-use the IndexedFile proof of concept to create the encrypted "file system"
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public abstract class SecureFileSystem extends IndexedFile {

    /**
     * @param desiredFile
     * @throws ChunkedMediumException
     */
    public SecureFileSystem(File desiredFile)
            throws ChunkedMediumException {
        super(desiredFile);
    }

    /**
     * Generates password suitable for use by a {@link SecureFileSystem}.
     *
     * @param rawPassword
     * @return
     */
    public static SecureString generatePassword(SecureString rawPassword) {
        return DualLayerEncryptionService.generateKeys(rawPassword);
    }

    /**
     * Use this in cases where you wish to avoid GC destroying the original, say between activities
     *
     * @param password
     * @return
     */
    public static SecureString copyPassword(SecureString password) {
        if (!(password instanceof KeySet)) {
            throw new MSSRuntime("Incompatible key types");
        }

        //	Cheesy but will have to be enough for now
        KeySet original = (KeySet) password;
        KeySet keySet = new KeySet();
        keySet.setKey(KeySet.KEYNUM.Key1, new SecureString(original.getKey(KeySet.KEYNUM.Key1).copyBytes()));
        keySet.setKey(KeySet.KEYNUM.Key2, new SecureString(original.getKey(KeySet.KEYNUM.Key2).copyBytes()));

        return keySet;
    }

    /**
     * Perform any emergency cleanup operations needed
     */
    @Override
    protected final void emergencyCleanup() {
        try {
            getPassword().finalize();
            SystemLog.get().error("Performed emergency password finalize!");
        } catch (Exception ex) {
            SystemLog.get().error("Could not finalize the password", ex);
        }
    }

    /**
     * Overridable logic for creating a new chained data unit
     */
    @Override
    protected final ChainedUnit getChainedDataUnit() {
        return new SecureDataUnit();
    }

    private ObjectEncryptor getEncryptionService() {
        return new DualLayerEncryptionService();
    }

    /**
     * Read in the encrypted data unit at the current location
     */
    @Override
    protected final ChainedUnit readDataUnit(long unitIndex) {
        Chunk chunk = readChunk(unitIndex);
        byte[] cipherText = chunk.getBytes();

        return (ChainedUnit) getEncryptionService().decryptObject(cipherText, getPassword());

    }

    @Override
    protected final void writeDataUnit(long chunkIndex, ChainedUnit dataUnit) {
        byte[] cipherText = getEncryptionService().encryptObject(getPassword(), dataUnit);
        super.writeBytes(chunkIndex, cipherText);
    }

    /**
     * Test that a given password is the same password as that used in this SFS
     *
     * @param password
     * @return
     */
    public final boolean testPassword(SecureString password) {
        SecureString keys = generatePassword(password);
        return getPassword().equals(keys);
    }

    /**
     * Implementations are STRONGLY encouraged to return a product returned by {@link #generatePassword(SecureString)}!
     *
     * @return
     */
    protected abstract SecureString getPassword();

    /**
     * Change the password of all data units to the given password.  This method will also invalidate the
     * current password once finished executing
     */
    public final void changePassword(final ProgressListener<Long> progress, final SecureString newPassword) {

        progress.setMax(super.getTotalUnits());

        Consumer<Pair<Long, ChainedUnit>> passwordChgCallback = (Pair<Long, ChainedUnit> object) -> {

            //	Current unit
            ChainedUnit currentUnit = object.second();

            //	And now re-store it with the new password!
            byte[] cipherText = getEncryptionService().encryptObject(newPassword, currentUnit);
            writeBytes(object.first(), cipherText);

            progress.update(object.first());
        };

        super.visitDataUnits(passwordChgCallback);

        getPassword().finalize();

    }

    /**
     * Destroys the password and closes this SFS.  Calling this will make this object useless
     */
    @Override
    public final void close() {
        super.close();
        if(this.getPassword() != null){
            this.getPassword().randomFinalize();
        }
    }

    /**
     * Toggle test mode (for unit tests only)
     *
     * @param testMode
     */
    final void setTestMode(boolean testMode) {
        super.testMode = testMode;
    }

    //  Provide this method in order to expose the logic for testing.  Do NOT modify
    //  this code

    /**
     * Kill the app and close the SFS
     */
    @Override
    protected final void errorOutOnLockTimeout() {
        super.errorOutOnLockTimeout();
    }
}