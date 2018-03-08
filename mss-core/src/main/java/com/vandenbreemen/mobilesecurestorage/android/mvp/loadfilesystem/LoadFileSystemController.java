package com.vandenbreemen.mobilesecurestorage.android.mvp.loadfilesystem;

import android.util.Log;

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials;
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError;

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
public class LoadFileSystemController {

    private LoadFileSystemModel model;

    private LoadFileSystemView view;

    public LoadFileSystemController(LoadFileSystemModel model, LoadFileSystemView view) {
        this.model = model;
        this.view = view;
    }

    public void providePassword(String password) {
        Single.create((SingleOnSubscribe<SFSCredentials>) e -> {
            try {
                e.onSuccess(model.providePassword(password));
            } catch (ApplicationError err) {
                Log.e("LoadFail", "Failed to Load", err);
                e.onError(err);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        view::onLoadSuccess,
                        e -> {
                            if (e instanceof ApplicationError) {
                                view.display((ApplicationError) e);
                            }
                        }
                );
    }
}
