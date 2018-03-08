package com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem;

import android.support.annotation.NonNull;
import android.util.Log;

import com.vandenbreemen.mobilesecurestorage.android.task.AsyncResult;
import com.vandenbreemen.mobilesecurestorage.file.ChunkedMediumException;
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.function.Consumer;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class CreateSecureFileSystemModel {

    /**
     * Password to be used
     */
    SecureString password;
    /**
     * What to do once the secure file system has been created
     */
    private Consumer<AsyncResult<SecureFileSystem>> secureFileSystemConsumer;
    /**
     * Location to create
     */
    private File location;
    private String fileName;

    /**
     * Constructor for use in production code
     *
     * @param location
     */
    public CreateSecureFileSystemModel(File location) {
        this.location = location;
    }

    public CreateSecureFileSystemModel(File location, Consumer<AsyncResult<SecureFileSystem>> resultListener) {
        this.location = location;
        this.secureFileSystemConsumer = resultListener;
    }

    public CreateSecureFileSystemModel setSecureFileSystemConsumer(Consumer<AsyncResult<SecureFileSystem>> secureFileSystemConsumer) {
        this.secureFileSystemConsumer = secureFileSystemConsumer;
        return this;
    }

    public void setFileName(String fileName) throws ApplicationError {
        if (StringUtils.isBlank(fileName)) {
            throw new ApplicationError("Filename is required");
        }
        this.fileName = fileName;
    }


    public void setPassword(SecureString password, SecureString confirmPassword) throws ApplicationError {
        if (!password.equals(confirmPassword)) {
            throw new ApplicationError("Passwords not same");
        }
        this.password = password;
    }

    private SecureFileSystem createSecureFileSystem() throws ChunkedMediumException {
        Log.i("Create SFS", "Creating SFS at path " + generateFile());
        return new SecureFileSystem(generateFile()) {
            @Override
            protected SecureString getPassword() {
                return password;
            }
        };
    }

    /**
     * Creates File based on the user's specified filename
     *
     * @return
     */
    @NonNull
    File generateFile() {
        return new File(location + File.separator + fileName);
    }

    public void create() {
        Single.create((SingleOnSubscribe<SecureFileSystem>) e -> {
            Log.i("Testing", "Creating the secure file sys");
            try {
                e.onSuccess(createSecureFileSystem());
            } catch (ChunkedMediumException ex) {
                Log.e("CreateFileSystem", "Failed to create new secure file system", ex);
                e.onError(ex);
            } catch (Exception ex) {
                Log.e("CreateFileSystem", "Unexpected error occurred", ex);
                e.onError(ex);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sfs -> {
                            Log.i("Testing", "Passing result on to subscriber");
                            secureFileSystemConsumer.accept(new AsyncResult(sfs));
                        },
                        err -> secureFileSystemConsumer.accept(new AsyncResult(err)));
    }
}
