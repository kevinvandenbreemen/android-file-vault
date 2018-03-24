package com.vandenbreemen.secretcamera.di

import com.vandenbreemen.secretcamera.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by kevin on 24/03/18.
 */
@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity():MainActivity

}