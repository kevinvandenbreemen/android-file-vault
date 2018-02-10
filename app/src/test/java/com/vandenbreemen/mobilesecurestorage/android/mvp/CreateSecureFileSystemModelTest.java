package com.vandenbreemen.mobilesecurestorage.android.mvp;

import android.os.Environment;

import com.vandenbreemen.mobilesecurestorage.android.mvp.createfilesystem.CreateSecureFileSystemModel;
import com.vandenbreemen.mobilesecurestorage.android.task.AsyncResult;
import com.vandenbreemen.mobilesecurestorage.security.SecureString;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;

import static junit.framework.TestCase.assertNotNull;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
@RunWith(RobolectricTestRunner.class)
public class CreateSecureFileSystemModelTest {
    ;
    /**
     * Listener for success
     */
    private SecureFileSystemCreated listener;

    @Before
    public void setup() {
        ShadowLog.stream = System.out;
        this.listener = new SecureFileSystemCreated();

        //  This is a workaround to deal with issue in which
        //  the success callback never gets called
        //  https://github.com/robolectric/robolectric/issues/2534
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> AndroidSchedulers.mainThread());
    }

    //  Exercise the core pattern participants
    @Test
    public void sanity() throws Exception {
        CreateSecureFileSystemModel model = new CreateSecureFileSystemModel(Environment.getExternalStorageDirectory(), listener);
        model.setFileName("toCreate");

        SecureString password = SecureFileSystem.generatePassword(new SecureString("aaa".getBytes()));

        model.setPassword(password, password);

        model.create();

        Awaitility.await().atMost(10, TimeUnit.MINUTES).until(() -> listener.created != null);

        assertNotNull("Created secure file system", listener.created.getResult());

    }

    private static class SecureFileSystemCreated implements Consumer<AsyncResult<SecureFileSystem>> {

        /**
         * Created SFS
         */
        private AsyncResult<SecureFileSystem> created;

        @Override
        public void accept(AsyncResult<SecureFileSystem> secureFileSystem) {
            this.created = secureFileSystem;
        }
    }

}
